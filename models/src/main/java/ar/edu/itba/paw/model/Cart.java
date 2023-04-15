package ar.edu.itba.paw.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> items = new ArrayList<>();

    public boolean addProduct(Product product) {
        return items.add(product);
    }

    public boolean removeProduct(Product product) {
        return items.remove(product);
    }
}
