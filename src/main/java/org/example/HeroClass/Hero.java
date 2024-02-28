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

    public double currentHp;
    public double maxHpOnCurrentAttributes;
    public double currentHpRegen;
    public double hpRegenOnCurrentAttributes;
    public double currentMana;
    public double maxManaOnCurrentAttributes;
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
    public double evasionChance = 0.0;
    public Map<String,Object> itemValues;
    // Maximum number of items is 6
    public int hudAttackSpeed;
    public Map<Integer, Item> items = new HashMap<>();

    private final Random RANDOM_GENERATOR = new Random();
    public Hero() {
        itemValues = new HashMap<>();
        itemValues.put("Intelligence",0.0); // ! Will always be int
        itemValues.put("Strength",0.0); // ! Will always be int
        itemValues.put("Agility",0.0); // ! Will always be int
        itemValues.put("Health",0.0); // ! Will always be int
        itemValues.put("Mana",0.0); // ! Will always be int
        itemValues.put("Armor",0.0); // ! Will always be int
        itemValues.put("Health Regeneration",0.0); // ! Decimal point
        itemValues.put("Mana Regeneration",0.0); // ! Decimal point
        itemValues.put("Attack Damage",0.0); // ! Will always be int
        itemValues.put("Attack Speed",0.0); // ! Will always be int
        itemValues.put("Magic Resistance",0.0); //? Percentage // ! Will always be int
        itemValues.put("Evasion",new ArrayList<Double>()); //? Percentage Kinda useless since it needs to be used differently in a formula
        itemValues.put("Max HP Health Regen",new HashMap<Double,Integer>()); //??  Percentage // ! Decimal point
        itemValues.put("Spell Lifesteal (Hero)",0.0); //? Percentage
        itemValues.put("Mana Regen Amp",0.0); //? Percentage
        itemValues.put("Base Attack Speed",0.0); //? Percentage
        itemValues.put("Health Regen Amp",new ArrayList<Double>()); //? Percentage // Sange based
        itemValues.put("Spell Damage Amp",0.0); //? Percentage
        itemValues.put("Lifesteal Amp",new ArrayList<Double>()); //? Percentage  // Sange based
        itemValues.put("Spell Lifesteal Amp",0.0); //? Percentage
        itemValues.put("Status Resistance",new ArrayList<Double>()); //? Percentage  // Sange based

        for(int i=1;i<=6;i++){
            items.put(i,null);
        }
    }

    public Map<String,String> attackEnemyHero(Hero enemy){
        // Finding the damage thats going to be dealt before reductions
        int randomDamage = RANDOM_GENERATOR.nextInt((currentDamageHigh - currentDamageLow) + 1) + currentDamageLow;

        int damage = (int) (randomDamage + (double) itemValues.get("Attack Damage")); // Because the bonus damage from items comes later

        return enemy.receiveDamage(damage, this);
    }
    public Map<String,String> receiveDamage(int damageDealtByEnemy , Hero attacker){

        // Damage block comes before
        // This damage block only works on melee heroes
        if(naturalDamageBlockPercentage > 0){
            double chance = RANDOM_GENERATOR.nextDouble();

            if(chance <= (double) naturalDamageBlockPercentage / 100){
                damageDealtByEnemy -= naturalDamageBlock;
            }
        }

        double damageReduced = (double) (damageDealtByEnemy * physicalDamageMultiplier);

        double damageAfterReductions = damageDealtByEnemy - damageReduced;


        if(evasionChance > 0.0 && checkChance(evasionChance)){
            damageAfterReductions = 0;
            return Map.of();
        }





        Map<String,String> map = Map.of(
                "Attacker",attacker.heroName,
                "Attacked",heroName,
                "DamageReceived",String.valueOf((int) damageAfterReductions),
                "Transition","(" + (int)currentHp + " -> " + (int)(currentHp - damageAfterReductions) + ")"
        );


        currentHp = roundToFixedDecimal(currentHp - damageAfterReductions,2);

        return map;
    }

    private boolean checkChance(double chance){
        return RANDOM_GENERATOR.nextDouble() <= chance;
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
        calculateCurrentHpWithItems();
        calculateCurrentHpRegenWithItems();
    }
    private void calculateCurrentHpWithItems(){
        maxHpOnCurrentAttributes = (int) (baseHp + (strenghtGainedFromLevel1toCurrentWithItems * EXTRA_HP_PER_STRENGTH_POINT) + (double) itemValues.get("Health"));
    }
    private void calculateCurrentHpRegenWithItems(){
        hpRegenOnCurrentAttributes = baseHpRegen + (strenghtGainedFromLevel1toCurrentWithItems * EXTRA_HP_REGEN_PER_STRENGTH_POINT) + (double) itemValues.get("Health Regeneration");
        Map<Double,Integer> tarrasqueBonuses = (Map<Double,Integer>) itemValues.get("Max HP Health Regen");

        if(tarrasqueBonuses!=null && !tarrasqueBonuses.isEmpty()){
            for(double key:tarrasqueBonuses.keySet()){
                double extraRegen = calculatePercentage(maxHpOnCurrentAttributes,key);
                hpRegenOnCurrentAttributes += extraRegen;
            }
        }
        List<Double> hpRegenAmps = (List<Double>) itemValues.get("Health Regen Amp");

        if(hpRegenAmps!=null && !hpRegenAmps.isEmpty()){
            double maxAmp = hpRegenAmps.stream().max(Double::compare).get();
            double amped = calculatePercentage(hpRegenOnCurrentAttributes,maxAmp);
            hpRegenOnCurrentAttributes += amped;
        }

    }
    // Example 100 * 40 / 100 = 40
    private double calculatePercentage(double value, double percentage){
        return value * percentage / 100;
    }

    private void calculateIntelligenceBasedBonuses(){
        calculateCurrentManaWithItems();
        calculateCurrentManaRegenWithItems();
        calculateCurrentMagicResistanceWithItems();
    }
    private void calculateCurrentManaRegenWithItems(){
        maxManaRegenOnCurrentAttributes = baseManaRegen + (intelligenceGainedFromLevel1toCurrentWithItems * EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT) + (double)itemValues.get("Mana Regeneration");

    }
    private void calculateCurrentManaWithItems(){
        maxManaOnCurrentAttributes = (int) (baseMana + (intelligenceGainedFromLevel1toCurrentWithItems * EXTRA_MANA_PER_INTELLIGENCE_POINT) + (double)itemValues.get("Mana"));
    }
    private void calculateCurrentMagicResistanceWithItems(){
        currentMagicResOnCurrentAttrtibutes = baseMagicResistance + (intelligenceGainedFromLevel1toCurrentWithItems * EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT) + (double)itemValues.get("Magic Resistance");
    }

    private void calculateAgilityBasedBonuses(){
        calculateAttackSpeedAndRate();
        calculateArmorBonuses();
    }
    // 168
    private void calculateAttackSpeedAndRate(){

        double atkSpeedSum1 = (
                baseAttackSpeed +
                ((baseAgilityPoints + agilityGainedFromLevel1toCurrentWithItems) *
                EXTRA_ATK_SPEED_PER_AGILITY_POINT)
        );
        double atkSpeedSum2 = (atkSpeedSum1 +
                calculatePercentage(atkSpeedSum1 ,(double) itemValues.get("Base Attack Speed"))) +
                (double) itemValues.get("Attack Speed");

        hudAttackSpeed = (int) atkSpeedSum2;

        atkSpeedSum2 = Math.max(20, Math.min(atkSpeedSum2, 700));

        currentAttackSpeed = (atkSpeedSum2) / (100.0 * BAT) ;

        currentAttackSpeed = roundToFixedDecimal(currentAttackSpeed,3);
        currentAttackRate = roundToFixedDecimal( 1 / currentAttackSpeed ,3);
    }
    private void calculateArmorBonuses(){
        currentArmor = baseArmor + (agilityGainedFromLevel1toCurrentWithItems * EXTRA_ARMOR_PER_AGILITY) + (double) itemValues.get("Armor");
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
        currentHpRegen = hpRegenOnCurrentAttributes;

        currentMana = maxManaOnCurrentAttributes;
        currentManaRegen = maxManaRegenOnCurrentAttributes;
    }

    /**
     *  Applying hp and mana regen to current hp and mana but not going past the maximum
     */
    public void regenerateHpAndMana() {
        currentHp = Math.min((currentHp + (currentHpRegen / 10)), maxHpOnCurrentAttributes);
        currentMana = Math.min((currentMana + (currentManaRegen / 10)), maxManaOnCurrentAttributes);
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

        // Strength Agility and Intelligence are double in the item values
        Double itemBonus = (Double) itemValues.get(attribute);

        return (itemBonus != null) ? attributesGained + itemBonus : attributesGained;
    }

    private double roundToFixedDecimal(double num, int decimalPlaces){
        double digits = Math.pow(10 , decimalPlaces);
        return Math.round(num * digits) / digits;
    }

    private static final Set<String> SPECIAL = Set.of("Max HP Health Regen","Evasion","Status Resistance","Lifesteal Amp","Health Regen Amp");
    //Multiple sange based items do not stack and the higher value takes priority
    private static final Set<String> SANGE_BASED_ITEMS = Set.of("Heaven's Halberd","Sange and Yasha","Kaya and Sange");
    public void updateHerosItem(Item item , boolean add,int inventorySlot) {

        if(add) {
            if(items.get(inventorySlot) != null) {
                updateHerosItem(item,false,inventorySlot);
            }
            items.put(inventorySlot,item);
        } else {
            items.put(inventorySlot,null);
        }


        for(String key:item.mapValues.keySet()){
            if(itemValues.containsKey(key)){
                double value = item.mapValues.get(key); // Value being added

                if(SPECIAL.contains(key)){
                    updateSpecialValue(key,value,add);
                }else{
                    double prev = (double) itemValues.get(key);  // Previous value

                    itemValues.put(key, prev + (add ? value : -value));

                    updateIfNecessary(key,value,add);
                }

            }


        }
    }
    private final Map<String,Runnable> ACTIONS_MAP = createActionsMap();
    private Map<String, Runnable> createActionsMap() {
        Map<String, Runnable> actionsMap = new HashMap<>();
        actionsMap.put("Agility", () -> {
            calculateAgilityGainedFromLevel1toCurrentWithItems();
            calculateAgilityBasedBonuses();
        });
        actionsMap.put("Strength", () -> {
            calculateStrengthGainedFromLevel1toCurrentWithItems();
            calculateStrengthBasedBonuses();
        });
        actionsMap.put("Intelligence", () -> {
            calculateIntelligenceGainedFromLevel1toCurrentWithItems();
            calculateIntelligenceBasedBonuses();
        });
        actionsMap.put("Armor",() -> calculateArmorBonuses());
        actionsMap.put("Health", () -> calculateStrengthBasedBonuses());
        actionsMap.put("Mana", () -> calculateCurrentManaWithItems());
        actionsMap.put("Magic Resistance", () -> calculateCurrentMagicResistanceWithItems());
        actionsMap.put("Health Regeneration", () -> calculateCurrentHpRegenWithItems());
        actionsMap.put("Mana Regeneration", () -> calculateCurrentManaRegenWithItems());
        actionsMap.put("Attack Speed", () -> calculateAttackSpeedAndRate());
        actionsMap.put("Base Attack Speed", () -> calculateAttackSpeedAndRate());
        return actionsMap;
    }
    private void updateSpecialValue(String key,double value,boolean add){
        switch (key){
            case "Evasion" ->{
                List<Double> evasion = (List<Double>) itemValues.get("Evasion");

                if(add) evasion.add(value / 100);
                else evasion.remove(value / 100);

                calculateEvasion();
            }
            case "Max Hp Health Regen" ->{
                // The percentage bonus as keys and the number of items that repeat as the value
                Map<Double,Integer> maxHpRegens = (Map<Double, Integer>) itemValues.get("Max Hp Health Regen");
                if (add) {
                    maxHpRegens.merge(value, 1, Integer::sum);
                } else {
                    maxHpRegens.computeIfPresent(value, (keyy, oldValue) -> {
                        int newValue = oldValue - 1;
                        if (newValue <= 0) {
                            maxHpRegens.remove(keyy);
                        }
                        return newValue;
                    });
                }

                calculateStrengthBasedBonuses();
            }
            case "Health Regen Amp" -> {
                List<Double> hpHegenAmp = (List<Double>) itemValues.get("Health Regen Amp");
                if(add) hpHegenAmp.add(value);
                else hpHegenAmp.remove(value);
                calculateStrengthBasedBonuses();
            }
            case "Lifesteal Amp" -> {
                List<Double> lifestealAmps = (List<Double>) itemValues.get("Lifesteal Amp");
                if(add) lifestealAmps.add(value);
                else lifestealAmps.remove(value);
                //calculateLifeSteal();
            }
            case "Status Resistance" ->{
                List<Double> statusResistances = (List<Double>) itemValues.get("Status Resistance");
                if(add) statusResistances.add(value);
                else statusResistances.remove(value);
                //calculateStatusResistance();
            }
        }
    }
    private void updateIfNecessary(String key,double value,boolean add) {
        ACTIONS_MAP.getOrDefault(key, () -> {}).run();
    }

    public void calculateEvasion() {
        List<Double> evasion = (List<Double>) itemValues.get("Evasion");
        if(evasion.isEmpty()){
            evasionChance = 0.0;
            return;
        }
        double tmp = 1.0;

        for (double value : evasion) {
            tmp *= (1.0 - value);
        }

        evasionChance = 1.0 - (1.0 - tmp);
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
