package br.com.sancrisxa.pic_pay_simplificado_senior.exception;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }
}
