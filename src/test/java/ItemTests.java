import org.example.ItemClass.Item;
import org.example.WebScrape.DataFetcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemTests {


    @Test
    public void testItemGivesItsCorrectValue(){
        Item item =DataFetcher.getItem("Eye of Skadi");

        assertEquals(22 , item.mapValues.get("Agility"));
        assertEquals(22 , item.mapValues.get("Strength"));
        assertEquals(22 ,  item.mapValues.get("Intelligence"));
        assertEquals(220 ,  item.mapValues.get("Health"));
        assertEquals(220 ,  item.mapValues.get("Mana"));
    }

    @Test
    public void testNeutralItemGivesCorrectValues(){
        Item item = DataFetcher.getItem("Defiant Shell");

        assertEquals(7, item.mapValues.get("Agility"));
        assertEquals(7 , item.mapValues.get("Strength"));
        assertEquals(7 , item.mapValues.get("Intelligence"));
        assertEquals(7 , item.mapValues.get("Armor"));
    }


}
