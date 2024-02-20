package org.example.ItemClass.PassiveEffectsImpl;

import org.example.HeroClass.Hero;
import org.example.ItemClass.PassiveEffect;

public class ArmorReductionEffect implements PassiveEffect {
    private int armorReduction;

    public ArmorReductionEffect(int armorReduction) {
        this.armorReduction = armorReduction;
    }

    @Override
    public void applyEffect(Hero hero) {
        
    }
}