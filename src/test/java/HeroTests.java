import org.example.Fight;
import org.example.HeroClass.Hero;
import org.example.ItemClass.Item;
import org.example.DataFetch.DataFetcher;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

import static org.example.HeroClass.AttackType.MELEE;
import static org.example.HeroClass.AttackType.RANGED;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HeroTests {

    Hero marci;

    @BeforeEach
    public void init(){
        marci =  DataFetcher.getHero("Marci");
    }


    @DisplayName("Correct attribute values")
    @Order(1)
    @Test
    public void testForCorrectAttributesValues(){
        assertEquals(23,marci.baseStrengthPoints);
        assertEquals(20,marci.baseAgilityPoints);
        assertEquals(19,marci.baseIntelligencePoints);

    }

    @DisplayName("Correct hp/hp regen test")
    @Order(2)
    @Test
    public void testForHpAndHpRegen(){

        assertEquals(626,marci.currentHp);
        assertEquals(2.55,marci.currentHpRegen);

    }

    @DisplayName("Correct mana/mana regen test")
    @Order(3)
    @Test
    public void testForManaAndManaRegen(){
        assertEquals(303,marci.currentMana);
        assertEquals(0.95,marci.currentManaRegen);
    }

    @DisplayName("Attack speed related tests")
    @Order(4)
    @Test
    public void testForAtkSpeedRelatedValues(){
        assertEquals(100,marci.baseAttackSpeed);
        assertEquals(1.7,marci.BAT); // Base Attack Time

        assertEquals(0.706,marci.currentAttackSpeed);
        assertEquals(1.416,marci.currentAttackRate);
    }

    @DisplayName("Agility related tests")
    @Order(5)
    @Test
    public void agilityBasedTest(){
        assertEquals(3.33,marci.currentArmor);
        marci.updateToMatchLevel(15);
        // Its 7.53 in the website
        assertEquals(7.53,marci.currentArmor);
    }
    @DisplayName("Correct natural damage block on melee heroes")
    @Order(6)
    @Test
    public void naturalDamageBlockMelee(){

        assertEquals(marci.attackType, MELEE);
        assertEquals(16,marci.naturalDamageBlock);
        assertEquals(50,marci.naturalDamageBlockPercentage);

    }

    @DisplayName("Correct natural damage block on range heroes")
    @Order(7)
    @Test
    public void naturalDamageBlockRange(){

        Hero sniper = DataFetcher.getHero("Sniper");;


        assertEquals(sniper.attackType,RANGED);

        assertEquals(0,sniper.naturalDamageBlock);
        assertEquals(0,sniper.naturalDamageBlockPercentage);

    }
    @DisplayName("Level 1 and 15 Universal damage")
    @Order(8)
    @Test
    public void universalDamageTest(){
        assertEquals(50,marci.currentDamageLow);
        assertEquals(56,marci.currentDamageHigh);

        marci.updateToMatchLevel(15);
        assertEquals(112,marci.currentDamageLow);
        assertEquals(118,marci.currentDamageHigh);
    }

    @DisplayName("Level 1 and 15 Agility damage")
    @Order(9)
    @Test
    public void agilityDamageTest(){

        Hero arcWarden = DataFetcher.getHero("Arc Warden");;


        assertEquals(51,arcWarden.currentDamageLow);
        assertEquals(57,arcWarden.currentDamageHigh);

        arcWarden.updateToMatchLevel(15);
        assertEquals(93,arcWarden.currentDamageLow);
        assertEquals(99,arcWarden.currentDamageHigh);
    }

    @DisplayName("Level 1 and 15 Strength damage")
    @Order(10)
    @Test
    public void strengthDamageTest(){

        Hero alchemist = DataFetcher.getHero("Alchemist");


        assertEquals(50,alchemist.currentDamageLow);
        assertEquals(56,alchemist.currentDamageHigh);

        alchemist.updateToMatchLevel(15);
        assertEquals(87,alchemist.currentDamageLow);
        assertEquals(93,alchemist.currentDamageHigh);
    }

    @DisplayName("Level 1 and 15 Intelligence damage")
    @Order(11)
    @Test
    public void intelligenceDamageTest(){

        Hero pugna = DataFetcher.getHero("Pugna");


        assertEquals(47,pugna.currentDamageLow);
        assertEquals(54,pugna.currentDamageHigh);

        pugna.updateToMatchLevel(15);
        assertEquals(119,pugna.currentDamageLow);
        assertEquals(126,pugna.currentDamageHigh);
    }
    @Test
    @Order(12)
    public void testOnTerrorbladeWithAlacrityOnLevel8Matches(){
        Hero tb = DataFetcher.getHero("Terrorblade");

        tb.updateToMatchLevel(8);


        Item item = DataFetcher.getItem("blade_of_alacrity");
        tb.updateHerosItem(item,true,3);
        assertEquals(15,Math.round(tb.currentArmor));
    }

    @Test
    @Order(13)
    public void testOnTerrorbladeWithAlacrityAndBladeMailOnLevel8Matches(){
        Hero tb =DataFetcher.getHero("Terrorblade");

        tb.updateToMatchLevel(8);

        Item item = DataFetcher.getItem("blade_of_alacrity");
        Item item2 = DataFetcher.getItem("blade_mail");
        tb.updateHerosItem(item,true,3);
        tb.updateHerosItem(item2,true,4);

        assertEquals(22,Math.round(tb.currentArmor));
    }

    @Test
    @Order(14)
    public void testButterfliesOnLevel16Templar(){

        Hero ta =DataFetcher.getHero("Templar Assassin");

        ta.updateToMatchLevel(16);

        Item item = DataFetcher.getItem("butterfly");
        Item item2 = DataFetcher.getItem("butterfly");

        ta.updateHerosItem(item,true,1);
        ta.updateHerosItem(item2,true,2);
        assertEquals(337 , ta.hudAttackSpeed);
    }

    @DisplayName("Testing one sided fight")
    @Test
    public void testOneSidedFight(){
        Hero puck = DataFetcher.getHero("Puck");


        marci.updateToMatchLevel(15);
        int[] res = Fight.fight(marci , puck,1);
        int[] res2 = Fight.fight(puck , marci,1);

        assertArrayEquals(new int[]{1,0},res);
        assertArrayEquals(new int[]{0,1},res2);
    }

    @DisplayName("Testing one sided fight 15 times")
    @Test
    public void testOneSidedFight15Times(){
        Hero puck = DataFetcher.getHero("Puck");

        marci.updateToMatchLevel(15);

        Item divine1 = DataFetcher.getItem("rapier");
        Item divine2 = DataFetcher.getItem("rapier");
        marci.updateHerosItem(divine1,true,1);
        marci.updateHerosItem(divine2,true,2);

        int[] res = Fight.fight(marci,puck,15);
        int[] res2 = Fight.fight(puck,marci,15);

        assertArrayEquals(new int[]{15,0},res);
        assertArrayEquals(new int[]{0,15},res2);
    }

    //Testing on chaos knight because its the hero with the largest damage spread , difference of 30
    @DisplayName("Test items on Chaos Knight")
    @Test
    public void testOn2ChaosKnightsForRandomness(){

        Hero ck1 = DataFetcher.getHero("Chaos Knight");
        Hero ck2 = DataFetcher.getHero("Chaos Knight");



        Item sat = DataFetcher.getItem("satanic");
        ck1.updateHerosItem(sat,true,2);
        Item sange = DataFetcher.getItem("sange_and_yasha");
        ck1.updateHerosItem(sange,true,1);
        Item daed = DataFetcher.getItem("greater_crit");
        ck1.updateHerosItem(daed,true,3);
        int[] res = Fight.fight(ck1,ck2,15);

        assertNotEquals(0,res[0]);
        assertNotEquals(0,res[1]);
    }

}
