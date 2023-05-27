package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "order_items")
@IdClass(OrderItem.OrderItemId.class)
public class OrderItem {

    @Id
    @Column(name = "order_id", nullable = false)
    private long orderId;

    @Id
    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column
    private String comment;

    OrderItem() {
    }

    public OrderItem(Product product, int lineNumber, int quantity, String comment) {
        this.product = product;
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.comment = comment;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    static class OrderItemId implements Serializable {

        private long orderId;
        private int lineNumber;

        OrderItemId() {
        }

        public OrderItemId(long orderId, int lineNumber) {
            this.orderId = orderId;
            this.lineNumber = lineNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OrderItemId)) return false;
            OrderItemId oi = (OrderItemId) o;
            return orderId == oi.orderId && lineNumber == oi.lineNumber;
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderId, lineNumber);
        }
    }
}
