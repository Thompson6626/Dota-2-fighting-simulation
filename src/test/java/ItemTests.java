import org.example.HeroClass.Hero;
import org.example.ItemClass.Item;
import org.example.WebScrape.DataFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
public class ItemTests {


    @Test
    public void testItemGivesItsCorrectValue(){
        Item item =DataFetcher.updateAccordingToItem("Eye of Skadi");

        assertEquals(22 , item.getBonusAgility());
        assertEquals(22 , item.getBonusStrength());
        assertEquals(22 , item.getBonusIntelligence());
        assertEquals(220 , item.getBonusHealth());
        assertEquals(220 , item.getBonusMana());
    }

    @Test
    public void testNeutralItemGivesCorrectValues(){
        Item item = DataFetcher.updateAccordingToItem("Defiant Shell");

        assertEquals(7,item.getBonusAgility());
        assertEquals(7,item.getBonusStrength());
        assertEquals(7,item.getBonusIntelligence());
        assertEquals(7,item.getBonusArmor());
    }


}
