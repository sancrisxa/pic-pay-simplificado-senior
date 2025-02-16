package br.com.sancrisxa.pic_pay_simplificado_senior.authorization;

public record Authorization(String message) {

    public boolean isAuthorized() {
        return message.equals("Autorizado");
    }
}
