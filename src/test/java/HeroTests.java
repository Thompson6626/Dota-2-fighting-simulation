import org.example.Fight;
import org.example.HeroClass.Hero;
import org.example.WebScrape.DataFetcher;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HeroTests {

    Hero marci;

    @BeforeEach
    public void init(){
        marci = new Hero();

        DataFetcher.fillHeroStats(marci,"Marci");
    }


    @DisplayName("Correct attribute values")
    @Test
    @Order(1)
    public void testForCorrectAttributesValues(){
        assertEquals(23,marci.baseStrengthPoints);
        assertEquals(20,marci.baseAgilityPoints);
        assertEquals(19,marci.baseIntelligencePoints);

    }

    @DisplayName("Correct hp/hp regen test")
    @Test
    @Order(2)
    public void testForHpAndHpRegen(){

        assertEquals(626,marci.currentHp);
        assertEquals(2.55,marci.currentHpRegen);

    }

    @DisplayName("Correct mana/mana regen test")
    @Test
    @Order(3)
    public void testForManaAndManaRegen(){
        assertEquals(303,marci.currentMana);
        assertEquals(0.95,marci.currentManaRegen);
    }

    @DisplayName("Attack speed related tests")
    @Test
    @Order(4)
    public void testForAtkSpeedRelatedValues(){
        assertEquals(100,marci.baseAttackSpeed);
        assertEquals(1.7,marci.BAT); // Base Attack Time

        assertEquals(0.706,marci.currentAttackSpeed);
        assertEquals(1.416,marci.currentAttackRate);
    }

    @DisplayName("Agility related tests")
    @Test
    @Order(5)
    public void agilityBasedTest(){
        assertEquals(3.33,marci.currentArmor);
        marci.heroUpdateToMatchLevel(15);
        // Its 7.53 in the website
        assertEquals(7.54,marci.currentArmor);
    }

    @DisplayName("Level 1 and 15 Universal damage")
    @Test
    @Order(6)
    public void universalDamageTest(){
        assertEquals(50,marci.currentDamageLow);
        assertEquals(56,marci.currentDamageHigh);

        marci.heroUpdateToMatchLevel(15);
        assertEquals(112,marci.currentDamageLow);
        assertEquals(118,marci.currentDamageHigh);
    }

    @DisplayName("Level 1 and 15 Agility damage")
    @Test
    @Order(7)
    public void agilityDamageTest(){

        Hero arcWarden = new Hero();
        DataFetcher.fillHeroStats(arcWarden,"Arc Warden");

        assertEquals(51,arcWarden.currentDamageLow);
        assertEquals(57,arcWarden.currentDamageHigh);

        arcWarden.heroUpdateToMatchLevel(15);
        assertEquals(93,arcWarden.currentDamageLow);
        assertEquals(99,arcWarden.currentDamageHigh);
    }

    @DisplayName("Level 1 and 15 Strength damage")
    @Test
    @Order(8)
    public void strengthDamageTest(){

        Hero alchemist = new Hero();
        DataFetcher.fillHeroStats(alchemist,"Alchemist");

        assertEquals(50,alchemist.currentDamageLow);
        assertEquals(56,alchemist.currentDamageHigh);

        alchemist.heroUpdateToMatchLevel(15);
        assertEquals(87,alchemist.currentDamageLow);
        assertEquals(93,alchemist.currentDamageHigh);
    }

    @DisplayName("Level 1 and 15 Intelligence damage")
    @Test
    @Order(9)
    public void intelligenceDamageTest(){

        Hero pugna = new Hero();
        DataFetcher.fillHeroStats(pugna,"Pugna");

        assertEquals(47,pugna.currentDamageLow);
        assertEquals(54,pugna.currentDamageHigh);

        pugna.heroUpdateToMatchLevel(15);
        assertEquals(119,pugna.currentDamageLow);
        assertEquals(126,pugna.currentDamageHigh);
    }
    @DisplayName("Test correct natural damage block on melee heroes")
    @Test
    @Order(10)
    public void naturalDamageBlockMelee(){

        assertTrue(marci.isMelee);
        assertEquals(16,marci.naturalDamageBlock);
        assertEquals(50,marci.naturalDamageBlockPercentage);

    }

    @DisplayName("Test correct natural damage block on range heroes")
    @Test
    @Order(11)
    public void naturalDamageBlockRange(){

        Hero sniper = new Hero();
        DataFetcher.fillHeroStats(sniper,"Sniper");

        assertFalse(sniper.isMelee);

        assertEquals(0,sniper.naturalDamageBlock);
        assertEquals(0,sniper.naturalDamageBlockPercentage);

    }

    @DisplayName("Testing one sided fight")
    @Test
    @Order(12)
    public void testOneSidedFight(){
        Hero puck = new Hero();
        DataFetcher.fillHeroStats(puck,"Puck");

        marci.heroUpdateToMatchLevel(15);

        int[] res = Fight.fight(marci , puck,1);
        int[] res2 = Fight.fight(puck , marci,1);

        assertArrayEquals(new int[]{1,0},res);
        assertArrayEquals(new int[]{0,1},res2);
    }

    @DisplayName("Testing one sided fight 15 times")
    @Test@Order(13)
    public void testOneSidedFight10Times(){
        Hero puck = new Hero();
        DataFetcher.fillHeroStats(puck,"Puck");
        marci.heroUpdateToMatchLevel(15);

        int[] res = Fight.fight(marci,puck,15);
        int[] res2 = Fight.fight(puck,marci,15);

        assertArrayEquals(new int[]{15,0},res);
        assertArrayEquals(new int[]{0,15},res2);
    }

    //Testing on chaos knight because its the hero with the largest damage spread , difference of 30
    @DisplayName("Test randomness on 2 Chaos Knights")
    @Test
    @Order(14)
    public void testOn2ChaosKnightsForRandomness(){

        Hero ck1 = new Hero();
        Hero ck2 = new Hero();

        DataFetcher.fillHeroStats(ck1,"Chaos Knight");
        DataFetcher.fillHeroStats(ck2,"Chaos Knight");

        int[] res = Fight.fight(ck1,ck2,15);

        assertNotEquals(0,res[0]);
        assertNotEquals(0,res[1]);
    }

    @Test
    public void testOn2NagaSirens(){

        Hero naga1 = new Hero();
        Hero naga2 = new Hero();

        DataFetcher.fillHeroStats(naga1,"Naga Siren");
        DataFetcher.fillHeroStats(naga2,"Naga Siren");

        int[] res = Fight.fight(naga1,naga2,2);

        System.out.println("Naga 1 won "+res[0] + " times");
        System.out.println("Naga 2 won "+res[1] + " times");
    }


}
