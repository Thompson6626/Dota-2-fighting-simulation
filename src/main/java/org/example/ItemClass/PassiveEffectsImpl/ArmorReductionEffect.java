package org.example.ItemClass.PassiveEffectsImpl;

import org.example.HeroClass.Hero;
import org.example.ItemClass.PassiveEffect;

public class ArmorReductionEffect implements PassiveEffect {
    private int armorReductionAmount;
    private int armorReductionTime;

    public ArmorReductionEffect(int armorReductionAmount, int armorReductionTime) {
        this.armorReductionAmount = armorReductionAmount;
        this.armorReductionTime = armorReductionTime;
    }

    @Override
    public void applyEffect(Hero enemyHero) {
        
    }
}