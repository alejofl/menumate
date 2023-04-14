package ar.edu.itba.paw.model;

public class OrderType {

    private final long orderTypeId;

    private final String name;

    public OrderType(long orderTypeId, String name) {
        this.orderTypeId = orderTypeId;
        this.name = name;
    }

    public long getOrderTypeId() {
        return orderTypeId;
    }

    public String getName() {
        return name;
    }
}
