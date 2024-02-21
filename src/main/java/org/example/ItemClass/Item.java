package org.example.ItemClass;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class Item {

    public String name;

    public int bonusAgility;
    public int bonusStrength;
    public int bonusIntelligence;

    public double bonusManaRegeneration;
    public double bonusHealthRegeneration;

    public int bonusArmor;

    public int bonusHealth;
    private int bonusMana;

    public int bonusAttackDamage;
    public int bonusAttackSpeed;

    public int bonusEvasionPercentage;
    public int bonusMagicResistancePercentage;

    public int bonusLifeStealPercentage;

    public int bonusBaseAttackSpeedPercentage;

    public int healthRegenAmpPercentage;
    public int lifestealAmpPercentage;

    public int manaRegenAmpPercentage;

    public double bonusMaxHpHealthRegen;

    public int bonusManaPercentage;

    public int bonusSpellLifestealPercentage;



    // A map because some items dont have levels
    // The length of any list is the maximum level the specific item can get
    public Map<String, List<? extends Number>> bonusesOnLevel;
    public Integer maxLevel;

    public PassiveEffect passiveEffect;

    public Item(String name){
        this.name = name;
    }



}
