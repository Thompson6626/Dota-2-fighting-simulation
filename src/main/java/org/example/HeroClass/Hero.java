package org.example.HeroClass;

import org.example.ItemClass.Item;
import org.mockito.internal.matchers.Null;

import java.nio.charset.StandardCharsets;
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

    // Maximum number of items is 6
    public Map<String,Number> itemValues;
    public List<Item> items = new ArrayList<>(6);

    private static final Random RANDOM_GENERATOR = new Random();
    public Hero() {
        itemValues = new HashMap<>();
        itemValues.put("Intelligence",0);
        itemValues.put("Strength",0);
        itemValues.put("Agility",0);
        itemValues.put("Health",0);
        itemValues.put("Mana",0);
        itemValues.put("Armor",0);
        itemValues.put("Health Regeneration",0.0); // ! Decimal point
        itemValues.put("Mana Regeneration",0.0); // ! Decimal point
        itemValues.put("Attack Damage",0);
        itemValues.put("Attack Speed",0);
        itemValues.put("Magic Resistance",0); //? Percentage
        itemValues.put("Evasion",0); //? Percentage
        itemValues.put("Spell Lifesteal (Hero)",0); //? Percentage
        itemValues.put("Max HP Health Regen",0.0); //??  Percentage // ! Decimal point
        itemValues.put("Spell Lifesteal Amp",0); //? Percentage
        itemValues.put("Mana Regen Amp",0); //? Percentage
        itemValues.put("Spell Damage Amp",0); //? Percentage
        itemValues.put("Base Attack Speed",0); //? Percentage
        itemValues.put("Lifesteal Amp",0); //? Percentage
        itemValues.put("Status Resistance",0); //? Percentage
        itemValues.put("Health Regen Amp",0); //? Percentage

    }


    public Map<String,String> attackEnemyHero(Hero enemy){
        // Finding the damage thats going to be dealt before reductions
        int randomDamage = RANDOM_GENERATOR.nextInt(( this.currentDamageHigh - this.currentDamageLow) + 1) + this.currentDamageLow;

        int damage = randomDamage + (int) itemValues.get("bonusItemDamage"); // Because the bonus damage from items comes later

        return enemy.receiveDamage(damage, this);
    }
    public Map<String,String> receiveDamage(int damageDealtByEnemy , Hero attacker){

        int damageReduced = (int) (damageDealtByEnemy * this.physicalDamageMultiplier);

        int damageAfterReductions = damageDealtByEnemy - damageReduced;


        if(this.naturalDamageBlockPercentage > 0){
            double chance = RANDOM_GENERATOR.nextDouble();
            // 50 -> 0.5
            if(chance < (double) this.naturalDamageBlockPercentage / 100){
                damageAfterReductions -= this.naturalDamageBlock;
            }
        }

        Map<String,String> map = Map.of(
                "Attacker",attacker.heroName,
                "Attacked",this.heroName,
                "DamageReceived",String.valueOf(damageAfterReductions),
                "Transition","(" + this.currentHp + " -> " + (this.currentHp-damageAfterReductions) + ")"
        );


        this.currentHp -= damageAfterReductions;

        return map;
    }

    public void heroUpdateToMatchLevel(int level){
        level -= 1;
        currentLevel = level;

        /*
        // Updating current attribute points
        currentStrength = baseStrengthPoints + strengthGained + bonusItemStrength;
        currentAgility = baseAgilityPoints + agilityGained + bonusItemAgility;
        currentIntelligence = baseIntelligencePoints + intelligenceGained + bonusItemIntelligence;
         */

        calculateStrengthBasedBonuses();

        calculateIntelligenceBasedBonuses();

        toMaxAccordingToLevel();

        calculateAgilityBasedBonuses();

        //Just the damage you would get with attributes because  there's still bonus damage from items left
        calculateCurrentPossibleDamage();

    }

    private void calculateStrengthBasedBonuses(){
        double strengthGained = roundToFixedDecimal(this.strengthGainPerLevel * this.currentLevel , 1);
        // Strength
        maxHpOnCurrentAttributes = (int) (baseHp + ( (strengthGained + itemValues.get("Strength").intValue()) * EXTRA_HP_PER_STRENGTH_POINT) );
        maxHpRegenOnCurrentAttributes = baseHpRegen + ( (strengthGained + itemValues.get("Strength").intValue())  * EXTRA_HP_REGEN_PER_STRENGTH_POINT);
    }

    private void calculateIntelligenceBasedBonuses(){
        double intelligenceGained = roundToFixedDecimal(this.intelligenceGainPerLevel * this.currentLevel , 1);
        maxManaOnCurrentAttributes = (int) (baseMana + ((intelligenceGained + itemValues.get("Intelligence").intValue()) * EXTRA_MANA_PER_INTELLIGENCE_POINT));
        maxManaRegenOnCurrentAttributes = baseManaRegen + ((intelligenceGained +  itemValues.get("Intelligence").intValue()) * EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT);
        currentMagicResOnCurrentAttrtibutes = baseMagicResistance + ((intelligenceGained +  itemValues.get("Intelligence").intValue()) * EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT);
    }
    private void calculateAgilityBasedBonuses(){
        double agilityGained = roundToFixedDecimal(this.agilityGainPerLevel * this.currentLevel , 1);

        double atkSpeedSumFromAgility = (baseAgilityPoints + (agilityGained + itemValues.get("Agility").intValue()) * EXTRA_ATK_SPEED_PER_AGILITY_POINT) + (int) itemValues.get("Bonus Strength");

        //Formula found here https://dota2.fandom.com/wiki/Attack_Speed
        currentAttackSpeed = (baseAttackSpeed + (atkSpeedSumFromAgility)) / (100.0 * BAT) ;

        currentAttackSpeed = roundToFixedDecimal(currentAttackSpeed,3);

        //  https://dota2.fandom.com/wiki/Attack_Speed
        currentAttackRate = roundToFixedDecimal( 1 / currentAttackSpeed ,3);

        currentArmor = baseArmor + (agilityGained * EXTRA_ARMOR_PER_AGILITY) + itemValues.get("Agility").intValue();
        currentArmor = roundToFixedDecimal(currentArmor,2);

        // https://dota2.fandom.com/wiki/Attack_Speed
        physicalDamageMultiplier = (0.06 * currentArmor)/( 1 + 0.06 * Math.abs(currentArmor) );
    }

    private void calculateCurrentPossibleDamage() {
        // Rounding to just 1 decimal digit of accuracy
        double agilityGained = roundToFixedDecimal(this.agilityGainPerLevel * this.currentLevel , 1);

        double strengthGained = roundToFixedDecimal(this.strengthGainPerLevel * this.currentLevel , 1);

        double intelligenceGained = roundToFixedDecimal(this.intelligenceGainPerLevel * this.currentLevel , 1);


        currentDamageLow = baseDamageLow;
        currentDamageHigh = baseDamageHigh;

        int agiWithItems = (int) (agilityGained + itemValues.get("Strength").intValue());
        int strWithItems = (int) (strengthGained + itemValues.get("Agility").intValue());
        int intellWithItems = (int) (intelligenceGained + itemValues.get("Intelligence").intValue());

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
    public void toMaxAccordingToLevel(){

        currentHp = maxHpOnCurrentAttributes;
        currentHpRegen = maxHpRegenOnCurrentAttributes;

        currentMana = maxManaOnCurrentAttributes;
        currentManaRegen = maxManaRegenOnCurrentAttributes;
    }

    /**
     *  Applying hp and mana regen to current hp and mana but not going past the maximum
     */
    public void applyHpAndManaRegen() {
        currentHp = Math.min((int)(currentHp + currentHpRegen), maxHpOnCurrentAttributes);
        currentMana = Math.min((int)(currentMana + currentManaRegen), maxManaOnCurrentAttributes);
    }
    private double roundToFixedDecimal(double num, int decimalPlaces){
        double digits = Math.pow(10 , decimalPlaces);
        return Math.round(num * digits) / digits;
    }


    public void addAndUpdateItem(Item item) {
        this.items.add(item);

        for(String key:item.getBonusesOnLevel().keySet()){
            Number value = item.mapValues.get(key);
            Number prev = this.itemValues.get(key);

            if (value instanceof Integer) {
                this.itemValues.put(key, prev.intValue() + value.intValue());
            } else if (value instanceof Double) {
                this.itemValues.put(key, prev.doubleValue() + value.doubleValue());
            }
        }


    }




}
