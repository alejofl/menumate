package ar.edu.itba.paw.model;

public class OrderItem {
    private final Product product;
    private final int lineNumber;
    private final int quantity;
    private final String comment;

    public OrderItem(Product product, int lineNumber, int quantity, String comment) {
        this.product = product;
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }
}
