package org.example.HeroClass;

import org.example.DataFetch.DataFetcher;
import org.example.ItemClass.QuantityValueWrapper;
import org.example.ItemClass.Item;

import java.util.*;
import java.util.List;

import static org.example.DataFetch.DataFetcher.*;
import static org.example.HeroClass.PrimaryAttribute.*;
import static org.example.ItemClass.BonusKeywords.*;


public class Hero {
    public static final String DEFAULT_NAME = "Choose a hero";
    private static final int ACTIVE_INVENTORY_SLOTS = 6;
    public String heroName = DEFAULT_NAME;

    public int baseHp;
    public double baseHpRegen;

    public int baseMana;
    public double baseManaRegen;

    public double baseArmor;
    public int baseMagicResistance;

    public int baseDamageLow;
    public int baseDamageHigh;

    public AttackType attackType;
    public int naturalDamageBlock;
    public int naturalDamageBlockPercentage;

    public int baseAttackSpeed;
    /**
     * Base Attack Time
     */
    public double BAT;
    public double currentAttackSpeed;
    public double currentAttackRate;
    public double attackPoint;
    public int currentLevel;

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

    public double baseArmorLevel0;
    public double physicalDamageMultiplier;

    public PrimaryAttribute primaryAttribute;

    public int baseAgilityPoints;
    public double agilityGainPerLevel;

    public int baseStrengthPoints;
    public double strengthGainPerLevel;

    public int baseIntelligencePoints;
    public double intelligenceGainPerLevel;

    public double agilityGainedUntilCurrentLevel;
    public double strenghtGainedUntilCurrentLevel;
    public double intelligenceGainedUntilCurrentLevel;

    public double agilityGainedUntilCurrentLevelWithItems;
    public double strenghtGainedUntilCurrentLevelWithItems;
    public double intelligenceGainedUntilCurrentLevelWithItems;
    public double evasionChance = 0.0;
    public double restorationMultiplier = 100.0;
    public Map<String,Object> itemValues;
    public int hudAttackSpeed;
    public Map<Integer, Item> items;
    public Item neutralItem = null;
    //Multiple sange based items do not stack and the higher value takes priority
    private static final Map<String,List<String>> KAYA_SANGE_YASHA_DERIVATIVES;
    static {
        KAYA_SANGE_YASHA_DERIVATIVES = DataFetcher. getSangeKayaYashaDerivatives();
    }
    private static final Set<String> SANGE_DERIVATIVES;
    static {
        SANGE_DERIVATIVES = new HashSet<>(KAYA_SANGE_YASHA_DERIVATIVES.get("sange"));
    }
    private static final List<Integer> PLUS_TWO_ATTRIBUTES = List.of(26,24,23,22,21,19,17);
    private final Random RANDOM_GENERATOR;
    public boolean isDead = false;
    public Hero(){
        this(DEFAULT_NAME);
    }

    public Hero(String name) {
        heroName = name;
        RANDOM_GENERATOR = new Random();

        itemValues = new HashMap<>();
        itemValues.put(BONUS_INTELLIGENCE,0.0);
        itemValues.put(BONUS_STRENGTH,0.0);
        itemValues.put(BONUS_AGILITY,0.0);
        itemValues.put(BONUS_HEALTH,0.0);
        itemValues.put(BONUS_MANA,0.0);
        itemValues.put(BONUS_ARMOR,0.0);
        itemValues.put(BONUS_HEALTH_REGEN,0.0);
        itemValues.put(BONUS_MANA_REGEN,0.0);
        itemValues.put(BONUS_ATTACK_DAMAGE,0.0);
        itemValues.put(BONUS_ATTACK_SPEED,0.0);
        itemValues.put(BONUS_MAGIC_RES,0.0); //? Percentage
        itemValues.put(BONUS_EVASION,new ArrayList<Double>()); //? Percentage
        itemValues.put(BONUS_PERCENTAGE_HEALTH_REGEN,new HashMap<String, QuantityValueWrapper>()); //??  Percentage
        itemValues.put(BONUS_SPELL_LIFESTEAL,0.0); //? Percentage
        itemValues.put(BONUS_MANA_REGEN_AMP,0.0); //? Percentage
        itemValues.put(BONUS_ATTACK_SPEED_PERCENTAGE,0.0); //? Percentage
        itemValues.put(BONUS_LIFESTEAL,0.0); //? Percentage

        // lifesteal amp and hp regen amp
        // ? Percentage // Sange based
        itemValues.put(BONUS_HP_REGEN_AMP,new HashMap<String, QuantityValueWrapper>());
        itemValues.put(BONUS_LIFESTEAL_AMP,new HashMap<String, QuantityValueWrapper>());

        itemValues.put(BONUS_SPELL_AMPLIFICATION,0.0); //? Percentage
        itemValues.put(BONUS_SPELL_LIFESTEAL_AMP,0.0); //? Percentage
        itemValues.put(BONUS_STATUS_RESISTANCE,new HashMap<String, QuantityValueWrapper>()); //? Percentage  // Sange based

        itemValues.put(BONUS_CRIT_CHANCE,new HashMap<String,QuantityValueWrapper>());
        itemValues.put(BONUS_CRIT_MULTIPLIER , new HashMap<String,QuantityValueWrapper>());


        items = new HashMap<>(ACTIVE_INVENTORY_SLOTS);

        for(int i = 1; i <= ACTIVE_INVENTORY_SLOTS; i++){
            items.put(i,null);
        }
    }

    public Map<String,String> attackEnemyHero(Hero enemy){
        // Finding the damage thats going to be dealt before reductions
        if (!isDead){
            int randomDamage = RANDOM_GENERATOR.nextInt((currentDamageHigh - currentDamageLow) + 1) + currentDamageLow;

            int damage = (int) (randomDamage + (double) itemValues.get(BONUS_ATTACK_DAMAGE));

            Map<String, QuantityValueWrapper> crits = (Map<String, QuantityValueWrapper>) itemValues.get(BONUS_CRIT_MULTIPLIER);
            if (!crits.isEmpty()){
                String maxItemName = null;
                int maxPercentage = 0;
                for(String itemName : crits.keySet()){
                    if (crits.get(itemName).getValue() > maxPercentage){
                        maxItemName = itemName;
                        maxPercentage = (int) crits.get(itemName).getValue();
                    }
                }
                int critChance = (int) ((Map<String,QuantityValueWrapper>)
                        itemValues.get(BONUS_CRIT_CHANCE)).get(maxItemName).getValue();

                if (critChance > 0 && checkChance(critChance)){
                    damage += (int) calculatePercentage(damage,maxPercentage);
                }
            }
            Map<String,String> logs = enemy.receiveAttack(damage, this);

            if ((double) itemValues.get(BONUS_LIFESTEAL) > 0.0){
                stealLife(Integer.parseInt(logs.get("DamageReceived")));
            }
            return logs;
        }
        return Collections.emptyMap();
    }
    private void stealLife(int damageDone){
        double lifeStealPercentage =  (Double) this.itemValues.get(BONUS_LIFESTEAL);

        Map<String,QuantityValueWrapper> bonusLifestealAmps = (Map<String, QuantityValueWrapper>) itemValues.get(BONUS_LIFESTEAL_AMP);

        if (!bonusLifestealAmps.isEmpty()){
            double highestFromSange = 0.0;
            for (String itemName : bonusLifestealAmps.keySet()){
                QuantityValueWrapper QVW = bonusLifestealAmps.get(itemName);
                if (SANGE_DERIVATIVES.contains(itemName)){
                    highestFromSange = Math.max(highestFromSange,QVW.getValue());
                }else{
                    lifeStealPercentage += calculatePercentage(lifeStealPercentage,QVW.getValue());
                }
            }
            lifeStealPercentage += calculatePercentage(lifeStealPercentage,highestFromSange);
        }

        int lifeStolen = (int) calculatePercentage(damageDone, lifeStealPercentage);
        this.currentHp = Math.min(this.currentHp + lifeStolen , this.maxHpOnCurrentAttributes);
    }
    public Map<String,String> receiveAttack(int damageDealtByEnemy , Hero attacker){
        // If attack is evaded
        if(evasionChance > 0.0 && checkChance(evasionChance * 100))
            return Collections.emptyMap();

        if(naturalDamageBlockPercentage > 0 && checkChance(naturalDamageBlockPercentage))
            damageDealtByEnemy -= naturalDamageBlock;


        double damageReduced = (double) (damageDealtByEnemy * physicalDamageMultiplier);
        double damageAfterReductions = damageDealtByEnemy - damageReduced;
        Map<String,String> map = Map.of(
                "DamageReceived", String.valueOf((int) damageAfterReductions),
                "Transition", String.format(
                        "(%d -> %d)",
                        (int) currentHp,
                        ((int) (currentHp - damageAfterReductions))
                ));

        currentHp = roundToFixedDecimal(currentHp - damageAfterReductions,2);

        if (currentHp <= 0) isDead = true;

        return map;
    }

    private boolean checkChance(double chance){
        return RANDOM_GENERATOR.nextInt(100) < chance;
    }

    public void updateToMatchLevel(int level){
        currentLevel = level;

        //With items
        calculateAgilityGainedUntilCurrentLevel();
        calculateStrengthGainedUntilCurrentLevel();
        calculateIntelligenceGainedUntilCurrentLevel();

        //Without items
        calculateAgilityGainedUntilCurrentLevelWithItems();
        calculateStrengthGainedUntilCurrentLevelWithItems();
        calculateIntelligenceGainedUntilCurrentLevelWithItems();

        calculateStrengthBasedBonuses();

        calculateIntelligenceBasedBonuses();

        calculateAgilityBasedBonuses();

        maxHpAndManaAccordingToCurrentLevel();

        //Just the damage you would get with attributes because  there's still bonus damage from items left
        calculateCurrentPossibleDamage();

    }
    private void calculateStrengthBasedBonuses(){
        // Strength
        calculateMaxCurrentHp();
        calculateCurrentHpRegenWithItems();
    }
    private void calculateMaxCurrentHp(){
        maxHpOnCurrentAttributes = (int) (baseHp +
                (strenghtGainedUntilCurrentLevelWithItems * EXTRA_HP_PER_STRENGTH_POINT) +
                (double) itemValues.get(BONUS_HEALTH));
    }
    private void calculateCurrentHpRegenWithItems(){
        hpRegenOnCurrentAttributes =
                baseHpRegen +
                (strenghtGainedUntilCurrentLevelWithItems * EXTRA_HP_REGEN_PER_STRENGTH_POINT) +
                (double) itemValues.get(BONUS_HEALTH_REGEN);

        hpRegenOnCurrentAttributes = roundToFixedDecimal(hpRegenOnCurrentAttributes,2);
        Map<String, QuantityValueWrapper> tarrasqueBonuses = (Map<String, QuantityValueWrapper>) itemValues.get(BONUS_PERCENTAGE_HEALTH_REGEN);

        // The values of the tarrasqueBonuses map has the count of how many times the same item gives the bonus
        if(tarrasqueBonuses != null && !tarrasqueBonuses.isEmpty()){
            for(String itemName:tarrasqueBonuses.keySet()){
                QuantityValueWrapper qVW = tarrasqueBonuses.get(itemName);
                double extraRegen = calculatePercentage(maxHpOnCurrentAttributes,qVW.getValue());
                hpRegenOnCurrentAttributes += extraRegen;
            }
        }
        Map<String, QuantityValueWrapper> hpRegenAmps = (Map<String, QuantityValueWrapper>) itemValues.get(BONUS_HP_REGEN_AMP);

        double highestFromSange = 0.0;
        if(hpRegenAmps != null && !hpRegenAmps.isEmpty()){
            for (String itemName : hpRegenAmps.keySet()){
                QuantityValueWrapper qVW = hpRegenAmps.get(itemName);
                if (SANGE_DERIVATIVES.contains(itemName)){
                    highestFromSange = Math.max(highestFromSange , qVW.getValue());
                }else{
                    double amped = calculatePercentage(hpRegenOnCurrentAttributes,qVW.getValue());
                    hpRegenOnCurrentAttributes += amped;
                }
            }
            double amped = calculatePercentage(hpRegenOnCurrentAttributes,highestFromSange);
            hpRegenOnCurrentAttributes += amped;
        }
    }

    /**
     *
     * @param value
     * @param percentage
     * @return The precentage of the first value
     */
    private double calculatePercentage(double value, double percentage){
        return value * percentage / 100;
    }

    private void calculateIntelligenceBasedBonuses(){
        calculateCurrentManaWithItems();
        calculateCurrentManaRegenWithItems();
        calculateCurrentMagicResistanceWithItems();
    }
    private void calculateCurrentManaRegenWithItems(){
        maxManaRegenOnCurrentAttributes =
                baseManaRegen +
                (intelligenceGainedUntilCurrentLevelWithItems * EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT) +
                (double) itemValues.get(BONUS_MANA_REGEN);

        maxManaRegenOnCurrentAttributes = roundToFixedDecimal(maxManaRegenOnCurrentAttributes,2);
    }
    private void calculateCurrentManaWithItems(){
        maxManaOnCurrentAttributes =
                (int) (baseMana +
                (intelligenceGainedUntilCurrentLevelWithItems * EXTRA_MANA_PER_INTELLIGENCE_POINT) +
                (double) itemValues.get(BONUS_MANA));
    }
    private void calculateCurrentMagicResistanceWithItems(){
        currentMagicResOnCurrentAttrtibutes =
                baseMagicResistance +
                (intelligenceGainedUntilCurrentLevelWithItems * EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT) +
                (double) itemValues.get(BONUS_MAGIC_RES);
    }

    private void calculateAgilityBasedBonuses(){
        calculateAttackSpeedAndRate();
        calculateArmor();
    }
    // 168
    private void calculateAttackSpeedAndRate(){

        double atkSpeedSum1 = (
                baseAttackSpeed +
                (agilityGainedUntilCurrentLevelWithItems *
                EXTRA_ATK_SPEED_PER_AGILITY_POINT)
        );
        double atkSpeedSum2 =
                (atkSpeedSum1 +
                calculatePercentage(
                        atkSpeedSum1 ,
                        (double) itemValues.get(BONUS_ATTACK_SPEED_PERCENTAGE)
                )) +
                (double) itemValues.get(BONUS_ATTACK_SPEED);

        hudAttackSpeed = (int) atkSpeedSum2;

        atkSpeedSum2 = Math.max(20, Math.min(atkSpeedSum2, 700));

        currentAttackSpeed = (atkSpeedSum2) / (100.0 * BAT) ;

        currentAttackSpeed = roundToFixedDecimal(currentAttackSpeed,3);
        currentAttackRate = roundToFixedDecimal( 1 / currentAttackSpeed ,3);
    }
    private void calculateArmor(){

        currentArmor =
                baseArmor +
                armorFromAgility(agilityGainedUntilCurrentLevelWithItems) +
                (double) itemValues.get(BONUS_ARMOR);
        currentArmor = roundToFixedDecimal(currentArmor,2);

        physicalDamageMultiplier = (0.06 * currentArmor)/(1 + 0.06 * Math.abs(currentArmor));
    }

    private void calculateCurrentPossibleDamage() {

        currentDamageLow = baseDamageLow;
        currentDamageHigh = baseDamageHigh;

        int agiWithItems = (int) agilityGainedUntilCurrentLevelWithItems;
        int strWithItems = (int) strenghtGainedUntilCurrentLevelWithItems;
        int intellWithItems = (int) intelligenceGainedUntilCurrentLevelWithItems;

        int bonusAttribute = switch (primaryAttribute) {
            case AGILITY -> agiWithItems;
            case STRENGTH -> strWithItems;
            case INTELLIGENCE -> intellWithItems;
            case UNIVERSAL ->  (int) Math.round(
                    (agiWithItems + strWithItems + intellWithItems)
                    * EXTRA_DAMAGE_PER_ATTRIBUTE_FOR_UNIVERSAL
            );
            case UNKNOWN -> 0;
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
        if (!isDead){
            currentHp = Math.min((currentHp + (currentHpRegen / 10)), maxHpOnCurrentAttributes);
            currentMana = Math.min((currentMana + (currentManaRegen / 10)), maxManaOnCurrentAttributes);
        }
    }
    public double calculateAttributeGainedWithoutItems(double gainPerLevel, int basePoints){
        return roundToFixedDecimal(basePoints + (gainPerLevel * (currentLevel - 1)), 1);
    }
    public double calculateAttributeGainWithItems(PrimaryAttribute attribute){

        double attributesGained = switch (attribute){
            case AGILITY -> calculateAttributeGainedWithoutItems(agilityGainPerLevel,baseAgilityPoints);
            case STRENGTH -> calculateAttributeGainedWithoutItems(strengthGainPerLevel,baseStrengthPoints);
            case INTELLIGENCE -> calculateAttributeGainedWithoutItems(intelligenceGainPerLevel,baseIntelligencePoints);
            default -> throw new IllegalStateException("No attribute with name: " + attribute);
        };

        for (int level : PLUS_TWO_ATTRIBUTES) {
            attributesGained += (currentLevel >= level) ? 2 : 0;
        }

        Double itemBonus = (Double) switch (attribute){
            case STRENGTH -> itemValues.get(BONUS_STRENGTH);
            case AGILITY -> itemValues.get(BONUS_AGILITY);
            case INTELLIGENCE -> itemValues.get(BONUS_INTELLIGENCE);
            default -> null;
        };

        return (itemBonus != null) ? attributesGained + itemBonus : attributesGained;
    }

    private double roundToFixedDecimal(double num, int decimalPlaces){
        double digits = Math.pow(10 , decimalPlaces);
        return Math.round(num * digits) / digits;
    }

    private static final List<String> SPECIAL = List.of(
            BONUS_PERCENTAGE_HEALTH_REGEN,
            BONUS_EVASION,
            BONUS_STATUS_RESISTANCE,
            BONUS_LIFESTEAL_AMP,
            BONUS_HP_REGEN_AMP,
            BONUS_CRIT_CHANCE,
            BONUS_CRIT_MULTIPLIER
    );


    public void updateHerosItem(Item item , boolean add,int inventorySlot) {
        if(item == null) return;

        if(inventorySlot <= 0 || inventorySlot > 6) {
            throw new IllegalArgumentException("Inventory slot not vlaid");
        }

        if(add) {
            if(items.get(inventorySlot) != null)
                updateHerosItem(item,false,inventorySlot);

            items.put(inventorySlot,item);
        } else {
            items.put(inventorySlot,null);
        }


        for(Map<String,Object> map : item.bonuses){
            // each map has 3 keys :  key,header and value
            for(String key : map.keySet()){
                String keyVal = (String) map.get(key);
                if(itemValues.containsKey(keyVal)){
                    double value = Double.parseDouble((String) map.get("value")); // Value being added

                    if(SPECIAL.contains(keyVal)){
                        updateSpecialValue(keyVal,item.name,value,add);
                    }else{
                        double prev = (double) itemValues.getOrDefault(keyVal,0.0);  // Previous value

                        itemValues.put(keyVal, prev + (add ? value : -value));

                        updateIfNecessary(keyVal,value,add);
                    }
                }
            }

        }

    }
    private final Map<String,Runnable> ACTIONS_MAP = createActionsMap();
    private Map<String, Runnable> createActionsMap() {
        Map<String, Runnable> actionsMap = new HashMap<>();
        actionsMap.put(BONUS_AGILITY, () -> {
            calculateAgilityGainedUntilCurrentLevelWithItems();
            calculateAgilityBasedBonuses();
        });
        actionsMap.put(BONUS_STRENGTH, () -> {
            calculateStrengthGainedUntilCurrentLevelWithItems();
            calculateStrengthBasedBonuses();
        });
        actionsMap.put(BONUS_INTELLIGENCE, () -> {
            calculateIntelligenceGainedUntilCurrentLevelWithItems();
            calculateIntelligenceBasedBonuses();
        });
        actionsMap.put(BONUS_ARMOR,() -> calculateArmor());
        actionsMap.put(BONUS_HEALTH, () -> calculateStrengthBasedBonuses());
        actionsMap.put(BONUS_MANA, () -> calculateCurrentManaWithItems());
        actionsMap.put(BONUS_MAGIC_RES, () -> calculateCurrentMagicResistanceWithItems());
        actionsMap.put(BONUS_HEALTH_REGEN, () -> calculateCurrentHpRegenWithItems());
        actionsMap.put(BONUS_MANA_REGEN, () -> calculateCurrentManaRegenWithItems());
        actionsMap.put(BONUS_ATTACK_SPEED, () -> calculateAttackSpeedAndRate());
        actionsMap.put(BONUS_ATTACK_SPEED_PERCENTAGE, () -> calculateAttackSpeedAndRate());
        return Collections.unmodifiableMap(actionsMap);
    }
    private void updateSpecialValue(
            String key,
            String itemName,
            double value,
            boolean add
    ){
        switch (key) {
            case BONUS_EVASION -> {
                List<Double> evasion = (List<Double>) itemValues.get(BONUS_EVASION);

                if (add) evasion.add(value);
                else evasion.remove(value);
                calculateEvasion();
            }
            case BONUS_PERCENTAGE_HEALTH_REGEN-> {
                // The percentage bonus as keys and the number of items that repeat as the value
                updateQuantity(itemName, key, add, value);
                calculateStrengthBasedBonuses();
            }
            case BONUS_HP_REGEN_AMP -> {
                // For Hp regen Amplification
                updateQuantity(itemName, BONUS_HP_REGEN_AMP, add, value);
                calculateStrengthBasedBonuses();
                // For lifesteal amplification
                updateQuantity(itemName, BONUS_LIFESTEAL_AMP, add, value);
            }
            case BONUS_STATUS_RESISTANCE -> {
                updateQuantity(itemName, key, add, value);
            }
            case BONUS_CRIT_CHANCE, BONUS_CRIT_MULTIPLIER -> {
                updateQuantity(itemName,key,add,value);
            }
        }
    }
    private void updateQuantity(
            String itemName,
            String key,
            boolean add,
            double value
    ){
        Map<String, QuantityValueWrapper> map = (Map<String, QuantityValueWrapper>) itemValues.get(key);
        map.compute(itemName, (k, oldValue) ->
                (add ?
                        ((oldValue == null) ? new QuantityValueWrapper(value) : oldValue.incrementQuantity())
                        :
                        ((oldValue != null) ?
                                (oldValue.decrementQuantity().getQuantity() <= 0 ? null : oldValue)
                                :
                                null)
                ));
    }
    private void updateIfNecessary(String key,double value,boolean add) {
        ACTIONS_MAP.getOrDefault(key, () -> {}).run();
    }

    public void calculateEvasion() {
        List<Double> evasion = (List<Double>) itemValues.get(BONUS_EVASION);
        if(evasion.isEmpty()){
            evasionChance = 0.0;
            return;
        }
        double tmp = 1.0;

        for (double value : evasion) {
            tmp *= (1.0 - roundToFixedDecimal(value / 100,2));
        }

        evasionChance = roundToFixedDecimal(1.0 - tmp,2);
    }

    public void calculateAgilityGainedUntilCurrentLevel() {
        agilityGainedUntilCurrentLevel = calculateAttributeGainedWithoutItems(agilityGainPerLevel,baseAgilityPoints);
    }
    public void calculateStrengthGainedUntilCurrentLevel() {
        strenghtGainedUntilCurrentLevel = calculateAttributeGainedWithoutItems(strengthGainPerLevel,baseStrengthPoints);
    }
    public void calculateIntelligenceGainedUntilCurrentLevel() {
        intelligenceGainedUntilCurrentLevel = calculateAttributeGainedWithoutItems(intelligenceGainPerLevel,baseIntelligencePoints);
    }
    public void calculateAgilityGainedUntilCurrentLevelWithItems() {
        agilityGainedUntilCurrentLevelWithItems = calculateAttributeGainWithItems(AGILITY);
    }
    public void calculateStrengthGainedUntilCurrentLevelWithItems() {
        strenghtGainedUntilCurrentLevelWithItems = calculateAttributeGainWithItems(STRENGTH);
    }
    public void calculateIntelligenceGainedUntilCurrentLevelWithItems() {
        intelligenceGainedUntilCurrentLevelWithItems = calculateAttributeGainWithItems(INTELLIGENCE);
    }


}
