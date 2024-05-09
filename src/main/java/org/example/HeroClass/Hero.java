package org.example.HeroClass;

import org.example.DataFetch.DataFetcher;
import org.example.DataFetch.Utils;
import org.example.ItemClass.BuffKeywords;
import org.example.ItemClass.DebuffKeywords;
import org.example.ItemClass.QuantityWrapper.QuantityValueAttrWrapper;
import org.example.ItemClass.QuantityWrapper.QuantityValueWrapper;
import org.example.ItemClass.Item;
import org.example.ItemClass.QuantityWrapper.QuantityWrapper;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class Hero {
    public static final String PLACEHOLDER_NAME = "Choose a hero";
    private static final int ACTIVE_INVENTORY_SLOTS = 6;
    public String heroName;
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

    public double currentLevelAgilityGainWithItems;
    public double currentLevelStrengthGainWithItems;
    public double currentLevelIntelligenceGainWithItems;
    public double evasionChance = 0.0;
    public double restorationMultiplier = 1.0;
    public Map<String,List<Double>> debuffs;
    public Map<String,Map<String,QuantityWrapper>> debuffsToApply;
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
    private double posRestorationMultiplier = 100.0;
    public Hero(){
        this(PLACEHOLDER_NAME);
    }

    public Hero(String name) {
        heroName = name;
        debuffs = new HashMap<>();
        RANDOM_GENERATOR = new Random();

        itemValues = new HashMap<>();
        itemValues.put(BuffKeywords.BONUS_INTELLIGENCE,0.0);
        itemValues.put(BuffKeywords.BONUS_STRENGTH,0.0);
        itemValues.put(BuffKeywords.BONUS_AGILITY,0.0);
        itemValues.put(BuffKeywords.BONUS_HEALTH,0.0);
        itemValues.put(BuffKeywords.BONUS_MANA,0.0);

        itemValues.put(BuffKeywords.BONUS_ARMOR,0.0);

        itemValues.put(BuffKeywords.BONUS_HEALTH_REGEN,0.0);
        itemValues.put(BuffKeywords.BONUS_MANA_REGEN,0.0);
        itemValues.put(BuffKeywords.BONUS_ATTACK_DAMAGE,0.0);
        itemValues.put(BuffKeywords.BONUS_ATTACK_SPEED,0.0);
        itemValues.put(BuffKeywords.BONUS_MAGIC_RES,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_EVASION,new ArrayList<Double>()); //? Percentage
        itemValues.put(BuffKeywords.BONUS_PERCENTAGE_HEALTH_REGEN,new HashMap<String, QuantityWrapper>()); //??  Percentage
        itemValues.put(BuffKeywords.BONUS_SPELL_LIFESTEAL,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_MANA_REGEN_AMP,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_ATTACK_SPEED_PERCENTAGE,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_LIFESTEAL,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_ALL_STATS,0.0);

        // lifesteal amp and hp regen amp
        // ? Percentage // Sange based
        itemValues.put(BuffKeywords.BONUS_HP_REGEN_AMP,new HashMap<String, QuantityWrapper>());
        itemValues.put(BuffKeywords.BONUS_LIFESTEAL_AMP,new HashMap<String, QuantityWrapper>());

        itemValues.put(BuffKeywords.BONUS_SPELL_AMPLIFICATION,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_SPELL_LIFESTEAL_AMP,0.0); //? Percentage
        itemValues.put(BuffKeywords.BONUS_STATUS_RESISTANCE,new HashMap<String, QuantityWrapper>()); //? Percentage  // Sange based

        itemValues.put(BuffKeywords.BONUS_CRIT_CHANCE,new HashMap<String,QuantityWrapper>());
        itemValues.put(BuffKeywords.BONUS_CRIT_MULTIPLIER , new HashMap<String,QuantityWrapper>());

        itemValues.put(BuffKeywords.BONUS_AURA_ARMOR , new HashMap<String,QuantityWrapper>());
        itemValues.put(BuffKeywords.BONUS_AURA_ATTACK_SPEED , new HashMap<String,QuantityWrapper>());


        items = new HashMap<>(ACTIVE_INVENTORY_SLOTS);

        debuffsToApply = new HashMap<>();

        for(int i = 1; i <= ACTIVE_INVENTORY_SLOTS; i++){
            items.put(i,null);
        }
    }
    public void applyDebbufsToEnemy(
            Hero enemy,
            Map<String,Map<String,QuantityWrapper>> toApply // Key : name of the generic debuff value : map with key: itemname, value:quantitywrapper
    ){
        Map<String,List<Double>> map = new HashMap<>();

        final String[] debuffKeyWords = {
                DebuffKeywords.ARMOR_REDUCTION,
                DebuffKeywords.RESTORATION_REDUCTION
        };

        for (String debuffKeyword : debuffKeyWords) {
            Map<String, QuantityWrapper> debuffItems = toApply.getOrDefault(debuffKeyword, Collections.emptyMap());
            if (!debuffItems.isEmpty()) {
                List<Double> values = debuffItems.values().stream()
                        .map(item -> ((QuantityValueWrapper) item).getValue())
                        .collect(Collectors.toList());
                map.put(debuffKeyword, values);
            }
        }
        Map<String, QuantityWrapper> attackSpeedReduction = toApply.getOrDefault(DebuffKeywords.ATTACK_SPEED_REDUCTION, Collections.emptyMap());
        if (!attackSpeedReduction.isEmpty()) {
            attackSpeedReduction.forEach((itemName, quantityWrapper) -> {
                List<Double> debuffValues = map.computeIfAbsent(DebuffKeywords.ATTACK_SPEED_REDUCTION, key -> new ArrayList<>());
                if (quantityWrapper instanceof QuantityValueAttrWrapper) {
                    QuantityValueAttrWrapper qVW = (QuantityValueAttrWrapper) quantityWrapper;
                    debuffValues.add(qVW.getValue(enemy.attackType));
                } else if (quantityWrapper instanceof QuantityValueWrapper) {
                    QuantityValueWrapper qVW = (QuantityValueWrapper) quantityWrapper;
                    debuffValues.add(qVW.getValue());
                }
            });
        }

        enemy.debuffs = map;
        enemy.calculateArmor();
        enemy.calculateRestorationMultiplier();
        enemy.calculateStrengthBasedBonuses();
        enemy.calculateAttackSpeedAndRate();
    }
    public Map<String,String> attackEnemyHero(Hero enemy){
        // Finding the damage thats going to be dealt before reductions
        int randomDamage = RANDOM_GENERATOR.nextInt((currentDamageHigh - currentDamageLow) + 1) + currentDamageLow;

        int damage = (int) (randomDamage + (double) itemValues.get(BuffKeywords.BONUS_ATTACK_DAMAGE));

        Map<String, QuantityWrapper> crits = (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_CRIT_MULTIPLIER);
        if (!crits.isEmpty()){
            String maxItemName = null;
            int maxPercentage = 0;
            for(String itemName : crits.keySet()){
                QuantityValueWrapper qVW = (QuantityValueWrapper) crits.get(itemName);
                if (qVW.getValue() > maxPercentage){
                    maxItemName = itemName;
                    maxPercentage = (int) qVW.getValue();
                }
            }
            QuantityValueWrapper qV = ((Map<String,QuantityValueWrapper>)
                    itemValues.get(BuffKeywords.BONUS_CRIT_CHANCE)).get(maxItemName);

            int critChance = (int) qV.getValue();

            if (critChance > 0 && Utils.checkChance(critChance,RANDOM_GENERATOR)){
                damage = (int) Utils.calculatePercentage(damage,maxPercentage);
            }
        }
        Map<String,String> logs = enemy.receiveAttack(damage, this);

        if ((double) itemValues.get(BuffKeywords.BONUS_LIFESTEAL) > 0.0){
            stealLife(Integer.parseInt(logs.get("DamageReceived")));
        }
        return logs;

    }
    private void stealLife(int damageDone){
        double lifeStealPercentage =  (Double) this.itemValues.get(BuffKeywords.BONUS_LIFESTEAL);
        lifeStealPercentage = Utils.calculatePercentage(lifeStealPercentage,restorationMultiplier * 100);
        int lifeStolen = (int) Utils.calculatePercentage(damageDone, lifeStealPercentage);
        this.currentHp = Math.min(this.currentHp + lifeStolen , this.maxHpOnCurrentAttributes);
    }
    public Map<String,String> receiveAttack(int damageDealtByEnemy , Hero attacker){
        // If attack is evaded
        if(evasionChance > 0.0 && Utils.checkChance(evasionChance * 100,RANDOM_GENERATOR))
            return Collections.emptyMap();


        if(naturalDamageBlockPercentage > 0 && Utils.checkChance(naturalDamageBlockPercentage,RANDOM_GENERATOR))
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

        currentHp = Utils.roundToDesiredDecimals(currentHp - damageAfterReductions,2);

        if (currentHp <= 0) isDead = true;

        return map;
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
                (currentLevelStrengthGainWithItems * DataFetcher.EXTRA_HP_PER_STRENGTH_POINT) +
                (double) itemValues.get(BuffKeywords.BONUS_HEALTH));
    }
    private void calculateCurrentHpRegenWithItems(){
        hpRegenOnCurrentAttributes =
                baseHpRegen +
                (currentLevelStrengthGainWithItems * DataFetcher.EXTRA_HP_REGEN_PER_STRENGTH_POINT) +
                (double) itemValues.get(BuffKeywords.BONUS_HEALTH_REGEN);

        hpRegenOnCurrentAttributes = Utils.roundToDesiredDecimals(hpRegenOnCurrentAttributes,2);

        hpRegenOnCurrentAttributes += Utils.sumFromMapOfQuantityWrappers(
                itemValues,
                BuffKeywords.BONUS_AURA_HEALTH_REGEN
        );

        Map<String, QuantityWrapper> bonusPercentageHealthRegen = (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_PERCENTAGE_HEALTH_REGEN);

        // The values of the tarrasqueBonuses map has the count of how many times the same item gives the bonus
        if(bonusPercentageHealthRegen != null && !bonusPercentageHealthRegen.isEmpty()){
            for(String itemName : bonusPercentageHealthRegen.keySet()){
                QuantityValueWrapper qVW = (QuantityValueWrapper) bonusPercentageHealthRegen.get(itemName);
                double extraRegen = Utils.calculatePercentage(maxHpOnCurrentAttributes,qVW.getValue());
                hpRegenOnCurrentAttributes += extraRegen;
            }
        }

        hpRegenOnCurrentAttributes = Utils.calculatePercentage(
                hpRegenOnCurrentAttributes,
                restorationMultiplier * 100
        );
        hpRegenOnCurrentAttributes = Utils.roundToDesiredDecimals(hpRegenOnCurrentAttributes,2);
    }


    private void calculateIntelligenceBasedBonuses(){
        calculateCurrentManaWithItems();
        calculateCurrentManaRegenWithItems();
        calculateCurrentMagicResistanceWithItems();
    }
    private void calculateCurrentManaRegenWithItems(){
        maxManaRegenOnCurrentAttributes =
                baseManaRegen +
                (currentLevelIntelligenceGainWithItems * DataFetcher.EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT) +
                (double) itemValues.get(BuffKeywords.BONUS_MANA_REGEN);

        maxManaRegenOnCurrentAttributes += Utils.sumFromMapOfQuantityWrappers(
                itemValues,
                BuffKeywords.BONUS_AURA_MANA_REGEN
        );

        maxManaRegenOnCurrentAttributes = Utils.roundToDesiredDecimals(maxManaRegenOnCurrentAttributes,2);
    }
    private void calculateCurrentManaWithItems(){
        maxManaOnCurrentAttributes =
                (int) (baseMana +
                (currentLevelIntelligenceGainWithItems * DataFetcher.EXTRA_MANA_PER_INTELLIGENCE_POINT) +
                (double) itemValues.get(BuffKeywords.BONUS_MANA));
    }
    private void calculateCurrentMagicResistanceWithItems(){
        currentMagicResOnCurrentAttrtibutes =
                baseMagicResistance +
                (currentLevelIntelligenceGainWithItems * DataFetcher.EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT) +
                (double) itemValues.get(BuffKeywords.BONUS_MAGIC_RES);
    }

    private void calculateAgilityBasedBonuses(){
        calculateAttackSpeedAndRate();
        calculateArmor();
    }
    // 168
    private void calculateAttackSpeedAndRate(){

        double atkSpeedSum = (
                baseAttackSpeed +
                (currentLevelAgilityGainWithItems *
                DataFetcher.EXTRA_ATK_SPEED_PER_AGILITY_POINT)
        );
        double atkSpeedAfterBonusPercentage =
                (atkSpeedSum +
                        Utils.calculatePercentage(
                        atkSpeedSum ,
                        (double) itemValues.get(BuffKeywords.BONUS_ATTACK_SPEED_PERCENTAGE)
                )) +
                (double) itemValues.get(BuffKeywords.BONUS_ATTACK_SPEED);

        hudAttackSpeed = (int) atkSpeedAfterBonusPercentage;

        atkSpeedAfterBonusPercentage += Utils.sumFromMapOfQuantityWrappers(
                itemValues,
                BuffKeywords.BONUS_AURA_ATTACK_SPEED
        );


        atkSpeedAfterBonusPercentage += debuffs.getOrDefault(DebuffKeywords.ATTACK_SPEED_REDUCTION,Collections.emptyList()).stream()
                .mapToDouble(Double::doubleValue)
                .sum();



        atkSpeedAfterBonusPercentage = Math.max(
                DataFetcher.MINIMUM_ATTACK_SPEED,
                Math.min(atkSpeedAfterBonusPercentage, DataFetcher.MAXIMUM_ATTACK_SPEED)
        );

        currentAttackSpeed = (atkSpeedAfterBonusPercentage) / (100.0 * BAT) ;

        currentAttackSpeed = Utils.roundToDesiredDecimals(currentAttackSpeed,3);
        currentAttackRate = Utils.roundToDesiredDecimals( 1 / currentAttackSpeed ,3);
    }
    private void calculateArmor(){

        currentArmor =
                baseArmor +
                DataFetcher.armorFromAgility(currentLevelAgilityGainWithItems) +
                (double) itemValues.get(BuffKeywords.BONUS_ARMOR);
        currentArmor = Utils.roundToDesiredDecimals(currentArmor,2);

        currentArmor += Utils.sumFromMapOfQuantityWrappers(
                itemValues,
                BuffKeywords.BONUS_AURA_ARMOR
        );

        currentArmor -= debuffs.getOrDefault(DebuffKeywords.ARMOR_REDUCTION,Collections.emptyList()).stream()
                .mapToDouble(Double::doubleValue)
                .sum();


        physicalDamageMultiplier = (0.06 * currentArmor) / (1 + 0.06 * Math.abs(currentArmor));
    }

    private void calculateCurrentPossibleDamage() {

        currentDamageLow = baseDamageLow;
        currentDamageHigh = baseDamageHigh;

        int agiWithItems = (int) currentLevelAgilityGainWithItems;
        int strWithItems = (int) currentLevelStrengthGainWithItems;
        int intellWithItems = (int) currentLevelIntelligenceGainWithItems;

        int bonusAttribute = switch (primaryAttribute) {
            case AGILITY -> agiWithItems;
            case STRENGTH -> strWithItems;
            case INTELLIGENCE -> intellWithItems;
            case UNIVERSAL ->  (int) Math.round(
                    (agiWithItems + strWithItems + intellWithItems)
                    * DataFetcher.EXTRA_DAMAGE_PER_ATTRIBUTE_FOR_UNIVERSAL
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
        return Utils.roundToDesiredDecimals(basePoints + (gainPerLevel * (currentLevel - 1)), 1);
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
            case STRENGTH -> itemValues.get(BuffKeywords.BONUS_STRENGTH);
            case AGILITY -> itemValues.get(BuffKeywords.BONUS_AGILITY);
            case INTELLIGENCE -> itemValues.get(BuffKeywords.BONUS_INTELLIGENCE);
            default -> null;
        };
        if (itemBonus != null) attributesGained += itemBonus;

        Double allStatsBonus =(Double) itemValues.get(BuffKeywords.BONUS_ALL_STATS);
        if (allStatsBonus > 0.0){
            attributesGained += allStatsBonus;
        }

        return attributesGained;
    }


    public void updateHerosItem(Item item , boolean add,int inventorySlot) {
        if(item == null) return;

        if(inventorySlot < 0 || inventorySlot > 6) {
            throw new IllegalArgumentException("Inventory slot not vlaid");
        }
        if(add){
            if (inventorySlot >= 1){
                // Return if the item is already on that slot
                if (items.get(inventorySlot) != null && item.name.equals(items.get(inventorySlot).name)) return;

                if(items.get(inventorySlot) != null)
                    updateHerosItem(item,false,inventorySlot);

                items.put(inventorySlot,item);
            }else{
                if (item.name.equals(neutralItem.name)) return;

                if(neutralItem != null)
                    updateHerosItem(item,false,inventorySlot);

                neutralItem = item;
            }
        }else{
            if (inventorySlot >= 1){
                items.put(inventorySlot,null);
            }else{
                neutralItem = null;
            }
        }




        for(Map<String,Object> map : item.bonuses){
            // each map has 3 keys :  key,header and value
            for(String key : map.keySet()){
                String keyVal = (String) map.get(key);
                double value = Double.parseDouble((String) map.get("value")); // Value being added

                if(itemValues.containsKey(keyVal)){
                    if(DataFetcher.SPECIAL_BUFFS.contains(keyVal)){
                        updateSpecialBuff(keyVal,item.name,value,add);
                    }else{
                        double prev = (double) itemValues.getOrDefault(keyVal,0.0);  // Previous value

                        itemValues.put(keyVal, prev + (add ? value : -value));

                        updateIfNecessary(keyVal);
                    }
                }else if (DataFetcher.DEBUFFS.contains(keyVal)){
                    updateSpecialDebuff(keyVal,item.name,value,add);
                }
            }
        }

    }

    private void updateSpecialDebuff(String keyVal, String name, double value, boolean add) {
        switch (keyVal) {
            case DebuffKeywords.BLIGHT_STONE_ARMOR_REDUCTION,
                    DebuffKeywords.ASSAULT_CUIRASS_ARMOR_REDUCTION -> {
                Utils.updateDebuffToApply(
                        debuffsToApply,
                        DebuffKeywords.ARMOR_REDUCTION,
                        name,
                        value,
                        add
                );
            }
            case DebuffKeywords.SHIVAS_GUARD_REGEN_REDUCTION,
                    DebuffKeywords.EYE_OF_SKADI_REGEN_REDUCTION -> {
                Utils.updateDebuffToApply(
                        debuffsToApply,
                        DebuffKeywords.RESTORATION_REDUCTION,
                        name,
                        value,
                        add
                );
            }
            case DebuffKeywords.SHIVAS_GUARD_ATTACK_SPEED_REDUCTION -> {
                Utils.updateDebuffToApply(
                        debuffsToApply,
                        DebuffKeywords.ATTACK_SPEED_REDUCTION,
                        name,
                        value,
                        add
                );
            }
            case  DebuffKeywords.EYE_OF_SKADI_MELEE_ATTACK_SPEED_REDUCTION,
                DebuffKeywords.EYE_OF_SKADI_RANGE_ATTACK_SPEED_REDUCTION -> {
                Utils.updateAttrDependantDebuff(
                        debuffsToApply,
                        DebuffKeywords.ATTACK_SPEED_REDUCTION,
                        keyVal,
                        name,
                        value,
                        add
                );
            }
        }
    }


    private final Map<String,Runnable> ACTIONS_MAP = createActionsMap();
    private Map<String, Runnable> createActionsMap() {
        Map<String, Runnable> actionsMap = new HashMap<>();
        actionsMap.put(BuffKeywords.BONUS_AGILITY, () -> {
            calculateAgilityGainedUntilCurrentLevelWithItems();
            calculateAgilityBasedBonuses();
        });
        actionsMap.put(BuffKeywords.BONUS_STRENGTH, () -> {
            calculateStrengthGainedUntilCurrentLevelWithItems();
            calculateStrengthBasedBonuses();
        });
        actionsMap.put(BuffKeywords.BONUS_INTELLIGENCE, () -> {
            calculateIntelligenceGainedUntilCurrentLevelWithItems();
            calculateIntelligenceBasedBonuses();
        });
        actionsMap.put(BuffKeywords.BONUS_ALL_STATS,() -> {
            calculateAgilityGainedUntilCurrentLevelWithItems();
            calculateAgilityBasedBonuses();
            calculateStrengthGainedUntilCurrentLevelWithItems();
            calculateStrengthBasedBonuses();
            calculateIntelligenceGainedUntilCurrentLevelWithItems();
            calculateIntelligenceBasedBonuses();
        });
        actionsMap.put(BuffKeywords.BONUS_ARMOR,() -> calculateArmor());
        actionsMap.put(BuffKeywords.BONUS_HEALTH, () -> calculateStrengthBasedBonuses());
        actionsMap.put(BuffKeywords.BONUS_MANA, () -> calculateCurrentManaWithItems());
        actionsMap.put(BuffKeywords.BONUS_MAGIC_RES, () -> calculateCurrentMagicResistanceWithItems());
        actionsMap.put(BuffKeywords.BONUS_HEALTH_REGEN, () -> calculateCurrentHpRegenWithItems());
        actionsMap.put(BuffKeywords.BONUS_MANA_REGEN, () -> calculateCurrentManaRegenWithItems());
        actionsMap.put(BuffKeywords.BONUS_ATTACK_SPEED, () -> calculateAttackSpeedAndRate());
        actionsMap.put(BuffKeywords.BONUS_ATTACK_SPEED_PERCENTAGE, () -> calculateAttackSpeedAndRate());
        return Collections.unmodifiableMap(actionsMap);
    }
    private void updateSpecialBuff(
            String key,
            String itemName,
            double value,
            boolean add
    ){
        switch (key) {
            case BuffKeywords.BONUS_EVASION -> {
                List<Double> evasion = (List<Double>) itemValues.get(BuffKeywords.BONUS_EVASION);

                if (add) evasion.add(value);
                else evasion.remove(value);
                calculateEvasion();
            }
            case BuffKeywords.BONUS_PERCENTAGE_HEALTH_REGEN-> {
                // The percentage bonus as keys and the number of items that repeat as the value
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(key),
                        add,
                        value
                );
                calculateStrengthBasedBonuses();
            }
            case BuffKeywords.BONUS_HP_REGEN_AMP -> {
                // For Hp regen Amplification
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_HP_REGEN_AMP),
                        add,
                        value
                );
                calculateRestorationMultiplier();
                calculateStrengthBasedBonuses();
                // For lifesteal amplification
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_LIFESTEAL_AMP),
                        add,
                        value
                );
                calculateRestorationMultiplier();
                calculateStrengthBasedBonuses();
            }
            case BuffKeywords.BONUS_STATUS_RESISTANCE -> {
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(key),
                        add,
                        value
                );
            }
            case BuffKeywords.BONUS_CRIT_CHANCE, BuffKeywords.BONUS_CRIT_MULTIPLIER -> {
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(key),
                        add,
                        value
                );
            }
            case BuffKeywords.BONUS_VLAD_ARMOR_AURA, BuffKeywords.BONUS_ASSAULT_CUIRASS_ARMOR_AURA -> {
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_AURA_ARMOR),
                        add,
                        value
                );
                calculateArmor();
            }
            case BuffKeywords.BONUS_ASSAULT_CUIRASS_ATTACK_SPEED -> {
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_AURA_ATTACK_SPEED),
                        add,
                        value
                );
                calculateAttackSpeedAndRate();
            }
            case BuffKeywords.HEADDRESS_BONUS_HEALTH_REGEN -> {
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_AURA_HEALTH_REGEN),
                        add,
                        value
                );
            }
            case BuffKeywords.BONUS_BASILIUS_MANA_REGEN -> {
                Utils.updateQuantity(
                        itemName,
                        (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_AURA_MANA_REGEN),
                        add,
                        value
                );
            }
        }
    }

    private void updateIfNecessary(String key) {
        ACTIONS_MAP.getOrDefault(key, () -> {}).run();
    }

    public void calculateEvasion() {
        List<Double> evasion = (List<Double>) itemValues.get(BuffKeywords.BONUS_EVASION);
        if(evasion.isEmpty()){
            evasionChance = 0.0;
            return;
        }
        double tmp = 1.0;

        for (double value : evasion) {
            tmp *= (1.0 - Utils.roundToDesiredDecimals(value / 100,2));
        }

        evasionChance = Utils.roundToDesiredDecimals(1.0 - tmp,2);
    }
    public void calculateRestorationMultiplier(){
        Map<String, QuantityWrapper> hpRegenAmps = (Map<String, QuantityWrapper>) itemValues.get(BuffKeywords.BONUS_HP_REGEN_AMP);

        List<Double> amps = new ArrayList<>();
        double highestFromSange = 0.0;
        if(hpRegenAmps != null && !hpRegenAmps.isEmpty()){
            for (String itemName : hpRegenAmps.keySet()){
                QuantityValueWrapper qVW = (QuantityValueWrapper) hpRegenAmps.get(itemName);

                if (SANGE_DERIVATIVES.contains(itemName))
                    highestFromSange = Math.max(highestFromSange , qVW.getValue());
                else
                    amps.add(qVW.getValue() / 100);
            }
            if(highestFromSange > 0.0) amps.add(highestFromSange / 100);
        }

        double tmp = 1.0;
        for (double value : amps){
            tmp *= 1.0 - value;
        }
        double positiveTotal = 1 - tmp;

        tmp = 1.0;
        if (debuffs.containsKey(DebuffKeywords.RESTORATION_REDUCTION)){
            for (double value : debuffs.get(DebuffKeywords.RESTORATION_REDUCTION)){
                tmp *= 1 - Utils.roundToDesiredDecimals(value / 100,1);
            }
        }
        double negativeTotal = 1 - tmp;

        restorationMultiplier = Utils.roundToDesiredDecimals(1 + (positiveTotal - negativeTotal),1);
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
        currentLevelAgilityGainWithItems = calculateAttributeGainWithItems(PrimaryAttribute.AGILITY);
    }
    public void calculateStrengthGainedUntilCurrentLevelWithItems() {
        currentLevelStrengthGainWithItems = calculateAttributeGainWithItems(PrimaryAttribute.STRENGTH);
    }
    public void calculateIntelligenceGainedUntilCurrentLevelWithItems() {
        currentLevelIntelligenceGainWithItems = calculateAttributeGainWithItems(PrimaryAttribute.INTELLIGENCE);
    }

}
