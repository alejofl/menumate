package ar.edu.itba.paw.model;

public enum TokenType {
    VERIFICATION_TOKEN("VERIFICATION_TOKEN"),
    RESET_PASSWORD_TOKEN("RESET_PASSWORD_TOKEN");

    private String messageCode;

    TokenType(String messageCode){
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
