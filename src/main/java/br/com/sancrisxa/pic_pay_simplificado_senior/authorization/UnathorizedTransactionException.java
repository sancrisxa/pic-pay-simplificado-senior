package br.com.sancrisxa.pic_pay_simplificado_senior.authorization;

public class UnathorizedTransactionException extends RuntimeException {
    public UnathorizedTransactionException(String message) {
        super(message);
    }
}
