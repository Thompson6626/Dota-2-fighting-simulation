package org.example.ItemClass.QuantityWrapper;

public class QuantityValueWrapper extends QuantityWrapper{
     private final double value;

     public QuantityValueWrapper(double value){
         super();
         this.value = value;
     }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "QuantityValueWrapper{" +
                "value=" + value +
                '}';
    }
}
