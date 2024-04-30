package org.example.DataFetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.HeroClass.Hero;
import org.example.ItemClass.Item;
import org.example.ItemClass.ItemTypes;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.HeroClass.AttackType.*;
import static org.example.HeroClass.PrimaryAttribute.*;
import static org.example.ItemClass.ItemTypes.NEUTRAL;
import static org.example.ItemClass.ItemTypes.PURCHASABLE;

public class DataFetcher {
    private DataFetcher(){}
    private static final List<Map<String,Object>> ALL_HEROES_INFO;
    static{
        String urlString = "https://api.opendota.com/api/heroStats";

        URI uri;
        List<Map<String,Object>> matches;
        try {
            uri = new URI(urlString);
            URL url = uri.toURL();

            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();
            ALL_HEROES_INFO = objectMapper.readValue(inputStream, List.class);

            inputStream.close();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static final int MAXIMUM_HERO_LEVEL;
    static{
        String urlString = "https://api.opendota.com/api/constants/xp_level";

        URI uri;
        try {
            uri = new URI(urlString);
            URL url = uri.toURL();

            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();

            MAXIMUM_HERO_LEVEL = objectMapper.readValue(inputStream, List.class).size();
            inputStream.close();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static final Map<String,Map<String,Object>> ALL_ITEMS;
    static {
        String urlString = "https://api.opendota.com/api/constants/items";

        URI uri;
        try {
            uri = new URI(urlString);
            URL url = uri.toURL();

            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();

            ALL_ITEMS = objectMapper.readValue(inputStream, Map.class);
            inputStream.close();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * <img  width = 25 src="https://static.wikia.nocookie.net/dota2_gamepedia/images/7/7a/Strength_attribute_symbol.png/revision/latest/scale-to-width-down/20?cb=20180323111829">ㅤStrength </img>
     */
    public static final int EXTRA_HP_PER_STRENGTH_POINT = 22;
    public static final double EXTRA_HP_REGEN_PER_STRENGTH_POINT = 0.1;

    /**
     * <img width = 25 src="https://static.wikia.nocookie.net/dota2_gamepedia/images/2/2d/Agility_attribute_symbol.png/revision/latest/scale-to-width-down/20?cb=20180323111717">ㅤAgility </img>
     */
    public static final double EXTRA_ARMOR_PER_AGILITY = 0.165;
    public static double armorFromAgility(double agility){
        return agility / 6;
    }
    public static final int EXTRA_ATK_SPEED_PER_AGILITY_POINT = 1;


    /**
     * <img width= 25 src="https://static.wikia.nocookie.net/dota2_gamepedia/images/5/56/Intelligence_attribute_symbol.png/revision/latest/scale-to-width-down/20?cb=20180323111753">ㅤIntelligence </img>
     */
    public static final int EXTRA_MANA_PER_INTELLIGENCE_POINT = 12;
    public static final double EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT = 0.05;
    public static final double EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT = 0.1;

    /**
     * <img  width = 25 src="https://static.wikia.nocookie.net/dota2_gamepedia/images/1/1c/Universal_attribute_symbol.png/revision/latest/scale-to-width-down/20?cb=20230501030320">ㅤUniversal </img>
     */
    public static final double EXTRA_DAMAGE_PER_ATTRIBUTE_FOR_UNIVERSAL = 0.7;

    public static List<String> getAllDotaHeroNames() {
        return ALL_HEROES_INFO.stream()
                .map(map -> {
                    String dName = (String) map.get("localized_name");
                    return Arrays.stream(dName.split("_"))
                            .map(str ->  Character.toUpperCase(str.charAt(0)) + str.substring(1))
                            .collect(Collectors.joining(" "));
                }).toList();
    }
    /**
     * Gets all item separated by its item type
     * @see ItemTypes
     * @return A list containing all the names of the items
     */
    public static Map<ItemTypes, List<String>> getItemsGroupedByType() {
        return Collections.unmodifiableMap(
                ALL_ITEMS.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().containsKey("tier") ? NEUTRAL : PURCHASABLE,
                        Collectors.mapping(
                                entry -> Arrays.stream(entry.getKey().split("_"))
                                        .map(str -> Character.toUpperCase(str.charAt(0)) + str.substring(1))
                                        .collect(Collectors.joining(" ")),
                                Collectors.toList()
                        )
                ))
        );
    }

    public static Map<String,List<String>> getSangeKayaYashaDerivatives(){
        Map<String,List<String>> swordsRelation = new HashMap<>();
        swordsRelation.put("kaya",new ArrayList<>());
        swordsRelation.put("sange",new ArrayList<>());
        swordsRelation.put("yasha",new ArrayList<>());

        for (String itemName : ALL_ITEMS.keySet()){
            for (String sword : swordsRelation.keySet()){
                if (
                    itemName.equals(sword) ||
                    ALL_ITEMS.get(itemName).containsKey("components") &&
                    ALL_ITEMS.get(itemName).get("components") != null &&
                    ((List<String>) ALL_ITEMS.get(itemName).get("components")).contains(sword)
                ){
                    swordsRelation.get(sword).add(itemName);
                }
            }
        }
        return Collections.unmodifiableMap(swordsRelation);
    }
    public static Hero getHero(String name){
        Hero hero = new Hero(name);
        Map<String,Object> heroStats = null;
        for (Map<String,Object> map : ALL_HEROES_INFO){
            Object value = map.get("localized_name");
            if (value instanceof String && value.equals(name)) {
                heroStats = map;
                break;
            }
        }

        String mainAttr = (String) heroStats.get("primary_attr");

        hero.primaryAttribute = switch (mainAttr){
            case "agi" ->  AGILITY;
            case "str" ->  STRENGTH;
            case "int" ->  INTELLIGENCE;
            case "all" ->  UNIVERSAL;
            default -> UNKNOWN;
        };

        String attckType = (String) heroStats.get("attack_type");
        hero.attackType = switch (attckType){
            case "Melee" -> MELEE;
            case "Ranged" -> RANGED;
            default -> VERSATILE;
        };
        if(hero.attackType == MELEE){
            hero.naturalDamageBlock = 16;
            hero.naturalDamageBlockPercentage = 50;
        }

        hero.agilityGainPerLevel = ((Number) heroStats.get("agi_gain")).doubleValue();
        hero.strengthGainPerLevel = ((Number) heroStats.get("str_gain")).doubleValue();
        hero.intelligenceGainPerLevel = ((Number) heroStats.get("int_gain")).doubleValue();
        hero.baseIntelligencePoints = (int) heroStats.get("base_int");
        hero.baseStrengthPoints = (int) heroStats.get("base_str");
        hero.baseAgilityPoints = (int) heroStats.get("base_agi");


        hero.baseMagicResistance = (int) heroStats.get("base_mr");

        hero.baseDamageLow = (int) heroStats.get("base_attack_min");
        hero.baseDamageHigh = (int) heroStats.get("base_attack_max");

        hero.baseAttackSpeed = ((Number) heroStats.get("base_attack_time")).intValue();
        hero.BAT = ((Number) heroStats.get("attack_rate")).doubleValue();
        hero.attackPoint = (double) heroStats.get("attack_point");

        hero.baseArmor = ((Number) heroStats.get("base_armor")).doubleValue();

        hero.baseHp = (int) heroStats.get("base_health");
        hero.baseHpRegen = ((Number) heroStats.get("base_health_regen")).doubleValue();

        hero.baseMana = (int) heroStats.get("base_mana");
        hero.baseManaRegen = ((Number) heroStats.get("base_mana_regen")).doubleValue();


        hero.updateToMatchLevel(1);

        return hero;
    }


    public static Item getItem(String itemName) {
        // attrib is an arraylist , each elemen in that arraylist is a linkedhashmap
        Map<String,Object> itemStats = ALL_ITEMS.getOrDefault(itemName,Collections.emptyMap());
        // Each map in the list has 3 keys (key , header and value)
        List<Map<String,Object>> itemSts = Collections.unmodifiableList(
                (List<Map<String, Object>>) itemStats.getOrDefault("attrib" , Collections.emptyList())
        );

        return new Item(itemName,itemSts);
    }



}
