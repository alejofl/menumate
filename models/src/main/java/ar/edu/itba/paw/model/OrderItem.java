package ar.edu.itba.paw.model;

import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    //    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //    @PrimaryKeyJoinColumn(name = "product_id", referencedColumnName = "product_id")
    //    private final Product product;
    @Id
    @Column(name = "product_id")
    private long productId;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(nullable = false)
    private int quantity;

    @Column
    private String comment;

    @Transient
    private Product product;

    OrderItem() {
    }

    public OrderItem(long productId, int lineNumber, int quantity, String comment) {
        this.productId = productId;
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.comment = comment;
    }

    public OrderItem(Product product, int lineNumber, int quantity, String comment) {
        this.product = product;
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }

    public long getProductId() {
        return productId;
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
