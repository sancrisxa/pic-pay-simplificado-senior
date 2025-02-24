package br.com.sancrisxa.pic_pay_simplificado_senior.transaction;

import br.com.sancrisxa.pic_pay_simplificado_senior.authorization.AuthorizerService;
import br.com.sancrisxa.pic_pay_simplificado_senior.notification.NotificationService;
import br.com.sancrisxa.pic_pay_simplificado_senior.wallet.Wallet;
import br.com.sancrisxa.pic_pay_simplificado_senior.wallet.WalletRepository;
import br.com.sancrisxa.pic_pay_simplificado_senior.wallet.WalletType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationservice) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationservice;
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        validate(transaction);

        var newTransaction = transactionRepository.save(transaction);

        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));

        authorizerService.authorize(transaction);
        notificationService.notify(newTransaction);

        return newTransaction;
    }

    private void validate(Transaction transaction) {
        LOGGER.info("validating transaction {}...", transaction);

        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                        .map(
                                payer -> payer.type() == WalletType.COMUM.getValue() &&
                                        payer.balance().compareTo(transaction.value()) >= 0 &&
                                        !payer.id().equals(transaction.payee()) ? true : null)
                        .orElseThrow(() -> new InvalidTransactionException(
                                "Invalid transaction - " + transaction)))
                .orElseThrow(() -> new InvalidTransactionException(
                        "Invalid transaction - " + transaction));
    }

    private static boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() && payer.balance().compareTo(transaction.value()) >= 0 && payer.id().equals(transaction.payee());
    }

    public List<Transaction> list() {
       return transactionRepository.findAll();
    }
}
