package org.example.HeroClass;

import java.util.Random;
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
    public double currentAttackRate;
    public int currentLevel = 1;

    public double currentArmor;

    public double currentMagicRes;

    public int currentHp;
    public int maxHpOnCurrentLevel;
    public double currentHpRegen;
    public double maxHpRegenOnCurrentLevel;
    public int currentMana;
    public int maxManaOnCurrentLevel;
    public double currentManaRegen;
    public double maxManaRegenOnCurrentLevel;
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


    public int bonusItemAgility = 0;
    public int bonusItemStrength = 0;
    public int bonusItemIntelligence = 0;

    public int bonusItemDamage = 0;


    public int bonusItemMagicRes = 0;
    public int bonusItemArmor = 0;

    public int bonusItemAtkSpeed = 0;

    // Item or hero passive abilities effects

    public int lifeStealPercentage = 0;



    public int stun_Chance = 0;
    public int stun_BonusDamage = 0;
    public int missChanceArea = 0;
    public int dodgeChance = 0;

    public int critChance = 0;
    public int critPercentage = 0;
    public int lifesteal = 0;

    public int javelinProcPercentage= 0;
    public int javelingProcDamage = 0;

    public int maelstromProcPercentage = 0;

    public int mjolnirProcPercentage = 0;

    public int gleipnirProcPercentage = 0;

    public int monkeyKingBarProcPercentage = 0;

    public int damageBlockPercentage = 0;
    public int damageBlockValue = 0;

    public int regenLifestealReductionArea = 0;

    public int regenLifestealReductionOnHit = 0;

    public int returnDamagePercentage = 0;

    public int manaBurnValue = 0;
    public int manaBurnDamage = 0;


    private static final Random RANDOM_GENERATOR = new Random();

    public void attackEnemyHero(Hero enemy){

        // Finding the damage thats going to be dealt before reductions
        int randomDamage = RANDOM_GENERATOR.nextInt(( this.currentDamageHigh - this.currentDamageLow) + 1) + this.currentDamageLow;

        int damage = randomDamage + this.bonusItemDamage;

        enemy.receiveDamage(damage);


    }
    public void receiveDamage(int damageDealtByEnemy){

        int damageReduced = (int) (damageDealtByEnemy * this.physicalDamageMultiplier);

        int damageAfterReductions = damageDealtByEnemy - damageReduced;

        RANDOM_GENERATOR.nextBoolean();
        if(this.naturalDamageBlockPercentage > 0){
            double chance = RANDOM_GENERATOR.nextDouble();
            // 50 -> 0.5
            if(chance < (double) this.naturalDamageBlockPercentage / 100){
                damageAfterReductions -= this.naturalDamageBlock;
            }
        }

        System.err.println(heroName + " gets hit with "+damageAfterReductions + " ("+currentHp + "->"+(currentHp-damageAfterReductions+")"));
        this.currentHp -= damageAfterReductions;

        System.out.println("-----------");
    }

    public void heroUpdateToMatchLevel(int level){
        level -= 1;
        currentLevel = level;

        // Rounding to just 1 decimal digit of accuracy
        double agilityGained = roundToFixedDecimal(agilityGainPerLevel * level , 1);

        double strengthGained = roundToFixedDecimal(strengthGainPerLevel * level , 1);

        double intelligenceGained = roundToFixedDecimal(intelligenceGainPerLevel * level , 1);

        // Updating current attribute points

        currentStrength = baseStrengthPoints + strengthGained + bonusItemStrength;
        currentAgility = baseAgilityPoints + agilityGained + bonusItemAgility;
        currentIntelligence = baseIntelligencePoints + intelligenceGained + bonusItemIntelligence;

        // Strength
        maxHpOnCurrentLevel = (int) (baseHp + ((strengthGained + bonusItemStrength) * EXTRA_HP_PER_STRENGTH_POINT));
        maxHpRegenOnCurrentLevel = baseHpRegen + ((strengthGained + bonusItemStrength)  * EXTRA_HP_REGEN_PER_STRENGTH_POINT);

        // Intelligence
        maxManaOnCurrentLevel = (int) (baseMana + ((intelligenceGained + bonusItemIntelligence) * EXTRA_MANA_PER_INTELLIGENCE_POINT));
        maxManaRegenOnCurrentLevel = baseManaRegen + ((intelligenceGained + bonusItemIntelligence) * EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT);

        toMaxAccordingToLevel();

        currentMagicRes = baseMagicResistance + ((intelligenceGained + bonusItemIntelligence) * EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT);

        // Agility
        double atkSpeedSumFromAgility = (baseAgilityPoints + (agilityGained + bonusItemAgility) * EXTRA_ATK_SPEED_PER_AGILITY_POINT) + bonusItemAtkSpeed;

        //Formula found here https://dota2.fandom.com/wiki/Attack_Speed
        currentAttackSpeed = (baseAttackSpeed + (atkSpeedSumFromAgility)) / (100.0 * BAT) ;

        currentAttackSpeed = roundToFixedDecimal(currentAttackSpeed,3);

        //  https://dota2.fandom.com/wiki/Attack_Speed
        currentAttackRate = roundToFixedDecimal( 1 / currentAttackSpeed ,3);


        currentArmor = baseArmor + (agilityGained * EXTRA_ARMOR_PER_AGILITY);
        currentArmor = roundToFixedDecimal(currentArmor,2);

        // https://dota2.fandom.com/wiki/Attack_Speed
        physicalDamageMultiplier = (0.06 * currentArmor)/( 1 + 0.06 * Math.abs(currentArmor) );

        //Just the damage you would get with attributes there's still raw bonus damage from items left
        calculateCurrentPossibleDamage(agilityGained,strengthGained,intelligenceGained);

    }


    private void calculateCurrentPossibleDamage(double agiGained, double strengthGained, double intellGained) {
        currentDamageLow = baseDamageLow;
        currentDamageHigh = baseDamageHigh;

        int agiWithItems = (int) (agiGained + bonusItemAgility);
        int strWithItems = (int) (strengthGained + bonusItemStrength);
        int intellWithItems = (int) (intellGained + bonusItemIntelligence);

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

        currentHp = maxHpOnCurrentLevel;
        currentHpRegen = maxHpRegenOnCurrentLevel;

        currentMana = maxManaOnCurrentLevel;
        currentManaRegen = maxManaRegenOnCurrentLevel;
    }

    /**
     *  Applying hp and mana regen to current hp and mana but not going past the maximum
     *
     */
    public void applyHpAndManaRegen() {
        currentHp = Math.min((int)(currentHp + currentHpRegen), maxHpOnCurrentLevel);
        currentMana = Math.min((int)(currentMana + currentManaRegen), maxManaOnCurrentLevel);
    }
    private double roundToFixedDecimal(double num, int decimalPlaces){
        double digits = Math.pow(10 , decimalPlaces);
        return Math.round(num * digits) / digits;
    }




}
