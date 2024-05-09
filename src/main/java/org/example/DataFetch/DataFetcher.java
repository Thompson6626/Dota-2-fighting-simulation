package org.example.DataFetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.HeroClass.AttackType;
import org.example.HeroClass.Hero;
import org.example.HeroClass.PrimaryAttribute;
import org.example.ItemClass.BuffKeywords;
import org.example.ItemClass.DebuffKeywords;
import org.example.ItemClass.Item;
import org.example.ItemClass.ItemTypes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class that gets information about the game from the OpenDota api , with static methods to retrieve them
 */
public class DataFetcher {
    private DataFetcher(){}

    /**
     * List containing all heroes with their stats
     */
    private static final List<Map<String,Object>> ALL_HEROES_INFO;
    static{
        String urlString = "https://api.opendota.com/api/heroStats";

        URI uri;
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

    /**
     * Get how much armor does the agility given generate
     */
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
    public static final int MINIMUM_ATTACK_SPEED = 20;
    public static final int MAXIMUM_ATTACK_SPEED = 700;

    /**
     *
     * @return An unmodifiable list containing the names of all the heroes in sorted order
     */
    public static List<String> getAllDotaHeroNames() {
        return ALL_HEROES_INFO.stream()
                .map(map -> {
                    String dName = (String) map.get("localized_name");
                    return Arrays.stream(dName.split("_"))
                            .map(str -> Character.toUpperCase(str.charAt(0)) + str.substring(1))
                            .collect(Collectors.joining(" "));
                }).sorted()
                .toList();
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
                        entry -> entry.getValue().containsKey("tier") ? ItemTypes.NEUTRAL : ItemTypes.PURCHASABLE,
                        Collectors.mapping(
                                entry -> Arrays.stream(entry.getKey().split("_"))
                                        .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                                        .collect(Collectors.joining(" ")),
                                Collectors.collectingAndThen(Collectors.toList(), list -> {
                                    Collections.sort(list);
                                    return list;
                                })
                        )
                ))
        );
    }

    /**
     *
     * @return A map with three keys:
     *       "Kaya", "Sange", and "Yasha".
     *        Each key maps to a list containing  all
     *       items that have the corresponding name as a component
     *       and the component itself
     */
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

    /**
     * Returns a new hero object with the correct stats according to the name
     * @param name The name of the hero
     * @return A new hero object with its respective values
     */
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
        assert heroStats != null;
        String mainAttr = (String) heroStats.get("primary_attr");

        hero.primaryAttribute = switch (mainAttr){
            case "agi" ->  PrimaryAttribute.AGILITY;
            case "str" ->  PrimaryAttribute.STRENGTH;
            case "int" ->  PrimaryAttribute.INTELLIGENCE;
            case "all" ->  PrimaryAttribute.UNIVERSAL;
            default -> PrimaryAttribute.UNKNOWN;
        };

        String attckType = (String) heroStats.get("attack_type");
        hero.attackType = switch (attckType){
            case "Melee" -> AttackType.MELEE;
            case "Ranged" -> AttackType.RANGED;
            default -> AttackType.VERSATILE;
        };
        if(hero.attackType == AttackType.MELEE){
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
    public static final List<String> SPECIAL_BUFFS = List.of(
            BuffKeywords.BONUS_PERCENTAGE_HEALTH_REGEN,
            BuffKeywords.BONUS_EVASION,
            BuffKeywords.BONUS_STATUS_RESISTANCE,
            BuffKeywords.BONUS_LIFESTEAL_AMP,
            BuffKeywords.BONUS_HP_REGEN_AMP,
            BuffKeywords.BONUS_CRIT_CHANCE,
            BuffKeywords.BONUS_CRIT_MULTIPLIER,
            BuffKeywords.BONUS_VLAD_ARMOR_AURA,
            BuffKeywords.BONUS_ASSAULT_CUIRASS_ARMOR_AURA,
            BuffKeywords.BONUS_ASSAULT_CUIRASS_ATTACK_SPEED,
            BuffKeywords.HEADDRESS_BONUS_HEALTH_REGEN,
            BuffKeywords.BONUS_BASILIUS_MANA_REGEN
    );
    public static final Set<String> DEBUFFS = Set.of(
            DebuffKeywords.BLIGHT_STONE_ARMOR_REDUCTION, // Blightstone,desolator,stydigan desolator  armor reduction
            DebuffKeywords.ASSAULT_CUIRASS_ARMOR_REDUCTION, // Assault cuirass armor reduction aura
            DebuffKeywords.SHIVAS_GUARD_REGEN_REDUCTION, // Shiva's regen reduction
            DebuffKeywords.EYE_OF_SKADI_REGEN_REDUCTION, // Skadi regen reduction
            DebuffKeywords.SHIVAS_GUARD_ATTACK_SPEED_REDUCTION, // Shivas attack speed reduction aura
            DebuffKeywords.EYE_OF_SKADI_RANGE_ATTACK_SPEED_REDUCTION, // Skadi attack speed red for range enemies
            DebuffKeywords.EYE_OF_SKADI_MELEE_ATTACK_SPEED_REDUCTION//  Skadi attack speed red for melee enemies
    );
    /**
     * Returns a new item object with the correct bonuses according to the name
     * @param name The name of the item
     * @return A new item object with its respective bonueses
     */
    public static Item getItem(String name) {
        // attrib is an arraylist , each elemen in that arraylist is a linkedhashmap
        Map<String,Object> itemStats = ALL_ITEMS.getOrDefault(name,Collections.emptyMap());
        // Each map in the list has 3 keys (key , header and value)
        List<Map<String,Object>> itemSts = Collections.unmodifiableList(
                (List<Map<String, Object>>) itemStats.getOrDefault("attrib" , Collections.emptyList())
        );

        return new Item(name,itemSts);
    }



}
