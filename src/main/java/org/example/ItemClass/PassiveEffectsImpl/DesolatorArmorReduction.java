package org.example.ItemClass.PassiveEffectsImpl;

import lombok.Getter;
import org.example.ItemClass.PassiveEffects.PassiveEffect;

public class DesolatorArmorReduction implements PassiveEffect {
    private int armorReductionAmount ;

    private int armorReductionTime;

    public DesolatorArmorReduction(int armorReductionAmount, int armorReductionTime) {
        this.armorReductionAmount = armorReductionAmount;
        this.armorReductionTime = armorReductionTime;
    }



}