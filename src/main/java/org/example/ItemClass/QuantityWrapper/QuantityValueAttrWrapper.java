package org.example.ItemClass.QuantityWrapper;

import org.example.HeroClass.AttackType;

public class QuantityValueAttrWrapper extends QuantityWrapper{

    private double meleeValue;
    private double rangeValue;

    public QuantityValueAttrWrapper() {
        super();
    }
    public double getValue(AttackType attackType){
        return attackType == AttackType.MELEE ? meleeValue : rangeValue;
    }
    public void setMeleeValue(double meleeValue) {
        this.meleeValue = meleeValue;
    }
    public void setRangeValue(double rangeValue) {
        this.rangeValue = rangeValue;
    }
}
