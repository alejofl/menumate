package ar.edu.itba.paw.model;

public enum OrderType {
    DINE_IN("dinein"),
    TAKEAWAY("takeaway"),
    DELIVERY("delivery");

    private final String messageCode;

    OrderType(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}