package org.example.HeroClass;

import org.example.ItemClass.Item;

import java.util.*;
import java.util.List;

import static org.example.WebScrape.DataFetcher.EXTRA_DAMAGE_PER_ATTRIBUTE_FOR_UNIVERSAL;
import static org.example.WebScrape.DataFetcher.EXTRA_ARMOR_PER_AGILITY;
import static org.example.WebScrape.DataFetcher.EXTRA_MANA_PER_INTELLIGENCE_POINT;
import static org.example.WebScrape.DataFetcher.EXTRA_HP_PER_STRENGTH_POINT;
import static org.example.WebScrape.DataFetcher.EXTRA_ATK_SPEED_PER_AGILITY_POINT;
import static org.example.WebScrape.DataFetcher.EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT;
import static org.example.WebScrape.DataFetcher.EXTRA_HP_REGEN_PER_STRENGTH_POINT;
import static org.example.WebScrape.DataFetcher.EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT;


public class Hero {

    public String heroName = "Choose a hero";

    public int baseHp;
    public double baseHpRegen;

    public int baseMana;
    public double baseManaRegen;

    public double baseArmor;
    public double baseMagicResistance;

    public int baseDamageLow;
    public int baseDamageHigh;

    public boolean isMelee;
    public int naturalDamageBlock;
    public int naturalDamageBlockPercentage;

    public int baseAttackSpeed;
    /**
     * Base Attack Time
     */
    public double BAT;

    public double currentAttackSpeed;
    /**
     * Time between attacks
     */
    public double currentAttackRate;
    /**
     * Substracted 1 to current level because the level 1 stats are already precalculated
     */
    public int currentLevel = 1;

    public double currentArmor;

    public double currentMagicResOnCurrentAttrtibutes;

    public int currentHp;
    public int maxHpOnCurrentAttributes;
    public double currentHpRegen;
    public double maxHpRegenOnCurrentAttributes;
    public int currentMana;
    public int maxManaOnCurrentAttributes;
    public double currentManaRegen;
    public double maxManaRegenOnCurrentAttributes;
    public double currentStrength;
    public double currentAgility;
    public double currentIntelligence;

    public int currentDamageLow;
    public int currentDamageHigh;

    public int mainArmor;
    public double physicalDamageMultiplier;

    public PrimaryAttribute primaryAttribute;

    public int baseAgilityPoints;
    public double agilityGainPerLevel;

    public int baseStrengthPoints;
    public double strengthGainPerLevel;

    public int baseIntelligencePoints;
    public double intelligenceGainPerLevel;

    public double agilityGainedFromLevel1toCurrent;
    public double strenghtGainedFromLevel1toCurrent;
    public double intelligenceGainedFromLevel1toCurrent;
    public double agilityGainedFromLevel1toCurrentWithItems;
    public double strenghtGainedFromLevel1toCurrentWithItems;
    public double intelligenceGainedFromLevel1toCurrentWithItems;
    // Maximum number of items is 6
    public Map<String,Double> itemValues;
    public List<Item> items = new ArrayList<>(6);
    private final Random RANDOM_GENERATOR = new Random();
    public Hero() {
        itemValues = new HashMap<>();
        itemValues.put("Intelligence",0.0);
        itemValues.put("Strength",0.0);
        itemValues.put("Agility",0.0);
        itemValues.put("Health",0.0);
        itemValues.put("Mana",0.0);
        itemValues.put("Armor",0.0);
        itemValues.put("Health Regeneration",0.0); // ! Decimal point
        itemValues.put("Mana Regeneration",0.0); // ! Decimal point
        itemValues.put("Attack Damage",0.0);
        itemValues.put("Attack Speed",0.0);
        itemValues.put("Magic Resistance",0.0); //? Percentage
        itemValues.put("Evasion",0.0); //? Percentage
        itemValues.put("Spell Lifesteal (Hero)",0.0); //? Percentage
        itemValues.put("Max HP Health Regen",0.0); //??  Percentage // ! Decimal point
        itemValues.put("Mana Regen Amp",0.0); //? Percentage
        itemValues.put("Base Attack Speed",0.0); //? Percentage
        itemValues.put("Health Regen Amp",0.0); //? Percentage
        itemValues.put("Spell Damage Amp",0.0); //? Percentage
        itemValues.put("Lifesteal Amp",0.0); //? Percentage
        itemValues.put("Spell Lifesteal Amp",0.0); //? Percentage
        itemValues.put("Status Resistance",0.0); //? Percentage

    }


    public Map<String,String> attackEnemyHero(Hero enemy){
        // Finding the damage thats going to be dealt before reductions
        int randomDamage = RANDOM_GENERATOR.nextInt((currentDamageHigh - currentDamageLow) + 1) + currentDamageLow;

        int damage = randomDamage + itemValues.get("Attack Damage").intValue(); // Because the bonus damage from items comes later

        return enemy.receiveDamage(damage, this);
    }
    public Map<String,String> receiveDamage(int damageDealtByEnemy , Hero attacker){

        int damageReduced = (int) (damageDealtByEnemy * physicalDamageMultiplier);

        int damageAfterReductions = damageDealtByEnemy - damageReduced;


        if(this.naturalDamageBlockPercentage > 0){
            double chance = RANDOM_GENERATOR.nextDouble();
            // 50 -> 0.5
            if(chance < (double) naturalDamageBlockPercentage / 100){
                damageAfterReductions -= naturalDamageBlock;
            }
        }

        Map<String,String> map = Map.of(
                "Attacker",attacker.heroName,
                "Attacked",heroName,
                "DamageReceived",String.valueOf(damageAfterReductions),
                "Transition","(" + currentHp + " -> " + (currentHp-damageAfterReductions) + ")"
        );


        currentHp -= damageAfterReductions;

        return map;
    }

    public void heroUpdateToMatchLevel(int level){
        level -= 1;
        currentLevel = level;

        calculateAgilityGainedFromLevel1toCurrent();
        calculateStrengthGainedFromLevel1toCurrent();
        calculateIntelligenceGainedFromLevel1toCurrent();

        calculateAgilityGainedFromLevel1toCurrentWithItems();
        calculateStrengthGainedFromLevel1toCurrentWithItems();
        calculateIntelligenceGainedFromLevel1toCurrentWithItems();

        /*
        // Updating current attribute points
        currentStrength = baseStrengthPoints + strengthGained + bonusItemStrength;
        currentAgility = baseAgilityPoints + agilityGained + bonusItemAgility;
        currentIntelligence = baseIntelligencePoints + intelligenceGained + bonusItemIntelligence;
         */

        calculateStrengthBasedBonuses();

        calculateIntelligenceBasedBonuses();

        calculateAgilityBasedBonuses();

        maxHpAndManaAccordingToCurrentLevel();

        //Just the damage you would get with attributes because  there's still bonus damage from items left
        calculateCurrentPossibleDamage();

    }

    private void calculateStrengthBasedBonuses(){
        // Strength
        maxHpOnCurrentAttributes = (int) (baseHp + (strenghtGainedFromLevel1toCurrentWithItems * EXTRA_HP_PER_STRENGTH_POINT) + itemValues.get("Health").intValue());
        maxHpRegenOnCurrentAttributes = baseHpRegen + (strenghtGainedFromLevel1toCurrentWithItems * EXTRA_HP_REGEN_PER_STRENGTH_POINT) + itemValues.get("Health Regeneration");
    }

    private void calculateIntelligenceBasedBonuses(){
        maxManaOnCurrentAttributes = (int) (baseMana + (intelligenceGainedFromLevel1toCurrentWithItems * EXTRA_MANA_PER_INTELLIGENCE_POINT) + itemValues.get("Mana").intValue());
        maxManaRegenOnCurrentAttributes = baseManaRegen + (intelligenceGainedFromLevel1toCurrentWithItems * EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT) + itemValues.get("Mana Regeneration");
        currentMagicResOnCurrentAttrtibutes = baseMagicResistance + (intelligenceGainedFromLevel1toCurrentWithItems * EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT) + itemValues.get("Magic Resistance").intValue();
    }
    private void calculateAgilityBasedBonuses(){

        double atkSpeedSum = (baseAgilityPoints + (agilityGainedFromLevel1toCurrentWithItems) * EXTRA_ATK_SPEED_PER_AGILITY_POINT) + itemValues.get("Attack Speed").intValue();

        //Formula found here https://dota2.fandom.com/wiki/Attack_Speed
        currentAttackSpeed = (baseAttackSpeed + (atkSpeedSum)) / (100.0 * BAT) ;

        currentAttackSpeed = roundToFixedDecimal(currentAttackSpeed,3);

        //  https://dota2.fandom.com/wiki/Attack_Speed
        currentAttackRate = roundToFixedDecimal( 1 / currentAttackSpeed ,3);

        currentArmor = baseArmor + (agilityGainedFromLevel1toCurrentWithItems * EXTRA_ARMOR_PER_AGILITY) + itemValues.get("Armor").intValue();
        currentArmor = roundToFixedDecimal(currentArmor,2);

        physicalDamageMultiplier = (0.06 * currentArmor)/( 1 + 0.06 * Math.abs(currentArmor));
    }

    private void calculateCurrentPossibleDamage() {

        currentDamageLow = baseDamageLow;
        currentDamageHigh = baseDamageHigh;

        int agiWithItems = (int) agilityGainedFromLevel1toCurrentWithItems;
        int strWithItems = (int) strenghtGainedFromLevel1toCurrentWithItems;
        int intellWithItems = (int) intelligenceGainedFromLevel1toCurrentWithItems;

        int bonusAttribute = switch (primaryAttribute) {
            case AGILITY -> agiWithItems;
            case STRENGTH -> strWithItems;
            case INTELLIGENCE -> intellWithItems;
            case UNIVERSAL ->  (int) Math.round(
                    (agiWithItems + strWithItems + intellWithItems)
                            * EXTRA_DAMAGE_PER_ATTRIBUTE_FOR_UNIVERSAL
            );
        };

        currentDamageLow += bonusAttribute;
        currentDamageHigh += bonusAttribute;
    }

    /**
     * Resetting hero's hp and mana to the maximum according to the current level
     */
    public void maxHpAndManaAccordingToCurrentLevel(){
        currentHp = maxHpOnCurrentAttributes;
        currentHpRegen = maxHpRegenOnCurrentAttributes;

        currentMana = maxManaOnCurrentAttributes;
        currentManaRegen = maxManaRegenOnCurrentAttributes;
    }

    /**
     *  Applying hp and mana regen to current hp and mana but not going past the maximum
     */
    public void regenerateHpAndMana() {
        currentHp = Math.min((int)(currentHp + currentHpRegen), maxHpOnCurrentAttributes);
        currentMana = Math.min((int)(currentMana + currentManaRegen), maxManaOnCurrentAttributes);
    }
    public double calculateAttributeGainWithoutItems(double gainPerLevel){
        return roundToFixedDecimal(gainPerLevel * currentLevel, 1);
    }
    public double calculateAttributeGainWithItems(String attribute){

        double attributesGained = switch (attribute){
            case "Agility" -> calculateAttributeGainWithoutItems(agilityGainPerLevel);
            case "Strength" -> calculateAttributeGainWithoutItems(strengthGainPerLevel);
            case "Intelligence" -> calculateAttributeGainWithoutItems(intelligenceGainPerLevel);
            default -> throw new IllegalStateException("No attribute with name: " + attribute);
        };

        Double itemBonus = itemValues.get(attribute);

        return (itemBonus != null) ? attributesGained + itemBonus : attributesGained;
    }

    private double roundToFixedDecimal(double num, int decimalPlaces){
        double digits = Math.pow(10 , decimalPlaces);
        return Math.round(num * digits) / digits;
    }

    public void updateHerosItem(Item item , boolean add) {
        this.items.add(item);

        for(String key:item.getBonusesOnLevel().keySet()){
            if(itemValues.containsKey(key)){
                double value = item.mapValues.get(key); // Previous value
                double prev = itemValues.get(key); // Value being added

                itemValues.put(key, prev + value);

                updateIfNecesary(key);
            }

        }

    }

    private void updateIfNecesary(String key) {
        switch (key){
            case "Agility" -> {
                calculateAgilityGainedFromLevel1toCurrentWithItems();
                calculateAgilityBasedBonuses();
            }
            case "Strength" -> {
                calculateStrengthGainedFromLevel1toCurrentWithItems();
                calculateStrengthBasedBonuses();
            }
            case "Intelligence" -> {
                calculateIntelligenceGainedFromLevel1toCurrentWithItems();
                calculateIntelligenceBasedBonuses();
            }

        }
    }
    public void calculateAgilityGainedFromLevel1toCurrent() {
        agilityGainedFromLevel1toCurrent = calculateAttributeGainWithoutItems(agilityGainPerLevel);
    }
    public void calculateStrengthGainedFromLevel1toCurrent() {
        strenghtGainedFromLevel1toCurrent = calculateAttributeGainWithoutItems(strengthGainPerLevel);
    }
    public void calculateIntelligenceGainedFromLevel1toCurrent() {
        intelligenceGainedFromLevel1toCurrent = calculateAttributeGainWithoutItems(intelligenceGainPerLevel);
    }
    public void calculateAgilityGainedFromLevel1toCurrentWithItems() {
        agilityGainedFromLevel1toCurrentWithItems = calculateAttributeGainWithItems("Agility");
    }
    public void calculateStrengthGainedFromLevel1toCurrentWithItems() {
        strenghtGainedFromLevel1toCurrentWithItems = calculateAttributeGainWithItems("Strength");
    }
    public void calculateIntelligenceGainedFromLevel1toCurrentWithItems() {
        intelligenceGainedFromLevel1toCurrentWithItems = calculateAttributeGainWithItems("Intelligence");
    }
}
