package org.example.ItemClass.QuantityWrapper;

public abstract class QuantityWrapper {

    private int quantity;

    public QuantityWrapper(){
        quantity = 1;
    }

    public QuantityWrapper incrementQuantity(){
        quantity++;
        return this;
    }
    public QuantityWrapper decrementQuantity(){
        quantity--;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "QuantityWrapper{" +
                "quantity=" + quantity +
                '}';
    }
}
