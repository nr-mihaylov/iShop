package edu.student.nrm.ishop.model;

/**
 * Created by Nikolay on 07-Dec-16.
 */

public class ShoppingItem {
    private String label;
    private int quantity;

    public ShoppingItem() {}

    public ShoppingItem(String label, int quantity) {
        this.label = label;
        this.quantity = quantity;
    }

    public String getLabel() {
        return this.label;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
