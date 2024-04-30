package org.example.ItemClass;

public class QuantityValueWrapper {
    private int quantity;
    private final double VALUE;

    public QuantityValueWrapper(double value){
        this.VALUE = value;
        quantity = 1;
    }
    public QuantityValueWrapper incrementQuantity(){
        quantity++;
        return this;
    }
    public QuantityValueWrapper decrementQuantity(){
        quantity--;
        return this;
    }
    public double getValue() {
        return VALUE;
    }
    public int getQuantity() {
        return quantity;
    }
    @Override
    public String toString() {
        return "CountValueWrapper{" +
                "quantity=" + quantity +
                ", value=" + VALUE +
                '}';
    }
}
