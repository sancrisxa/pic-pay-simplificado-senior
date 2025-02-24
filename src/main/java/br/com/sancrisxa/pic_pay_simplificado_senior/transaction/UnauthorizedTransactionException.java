package br.com.sancrisxa.pic_pay_simplificado_senior.transaction;

public class UnauthorizedTransactionException extends RuntimeException {
    public UnauthorizedTransactionException(String message) {
        super(message);
    }
}
