package ar.edu.itba.paw.webapp.form;

public enum OrderType {
    DINEIN(1),
    TAKEAWAY(2),
    DELIVERY(3);

    private final int id;

    OrderType(int id) {
        this.id = id;
    }
}
