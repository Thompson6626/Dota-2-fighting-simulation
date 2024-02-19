package org.example.WebScrape;

import org.example.HeroClass.Hero;
import org.example.HeroClass.MainAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFetcher {
    private static final List<List<Number>> ATTRIBUTE_GAINS_CONSTANTS = getAttributeGainsConstants();
    // Strength
    public static final int EXTRA_HP_PER_STRENGTH_POINT = (int) ATTRIBUTE_GAINS_CONSTANTS.get(0).get(0);
    public static final double EXTRA_HP_REGEN_PER_STRENGTH_POINT = (double) ATTRIBUTE_GAINS_CONSTANTS.get(0).get(1);
    // Agility
    public static final double EXTRA_ARMOR_PER_AGILITY = (double) ATTRIBUTE_GAINS_CONSTANTS.get(1).get(0);
    public static final int EXTRA_ATK_SPEED_PER_AGILITY_POINT = (int) ATTRIBUTE_GAINS_CONSTANTS.get(1).get(3);
    // Intelligence
    public static final int EXTRA_MANA_PER_INTELLIGENCE_POINT = (int) ATTRIBUTE_GAINS_CONSTANTS.get(2).get(0);
    public static final double EXTRA_MANA_REGEN_PER_INTELLIGENCE_POINT = (double) ATTRIBUTE_GAINS_CONSTANTS.get(2).get(1);
    public static final double EXTRA_MAGIC_RES_PER_INTELLIGENCE_POINT = (double) ATTRIBUTE_GAINS_CONSTANTS.get(2).get(2);
    //Universal
    public static final double EXTRA_DAMAGE_PER_ATTRIBUTE_FOR_UNIVERSAL = (double) ATTRIBUTE_GAINS_CONSTANTS.get(3).get(0);
    public static final int MAXIMUM_HERO_LEVEL = getMaximumPossibleHeroLevel();

    public static List<List<Number>> getAttributeGainsConstants() {

        try {
            Document doc = Jsoup.connect("https://dota2.fandom.com/wiki/Attributes").get();

            // 20 for strength
            // 21 for agility
            // 22 for intelligence
            // 23 for universal
            Elements lists = doc.select("ul");

            List<String> attributeString = List.of(
                    lists.get(20).text(),
                    lists.get(21).text(),
                    lists.get(22).text(),
                    lists.get(23).text()
            );

            List<List<Number>> attributeGains = new ArrayList<>();

            // Result will look something like this
            // [22, 0.1], [0.167, 1, 6, 1], [12, 0.05, 0.1], [0.7]]
            extractNumbers(attributeGains , attributeString);

            return attributeGains;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static void extractNumbers(List<List<Number>> result, List<String> inputs) {
        for (String str : inputs) {
            StringBuilder sb = new StringBuilder();
            List<Number> tmp = new ArrayList<>();

            for (char c : str.toCharArray()) {
                if (Character.isDigit(c) || c == '.' || c == '-') {
                    sb.append(c);

                } else if (!sb.isEmpty()) {
                    if(sb.charAt(sb.length()-1)=='.') sb.deleteCharAt(sb.length()-1);

                    if(!sb.isEmpty()) {
                        String numb = sb.toString();

                        if(numb.contains(".")) tmp.add(Double.parseDouble(numb));
                        else tmp.add(Integer.parseInt(numb));

                        sb.setLength(0);
                    }
                }
            }
            // Add the last number if the string ends with a number
            if (!sb.isEmpty()) {
                if(sb.charAt(sb.length()-1)=='.') sb.deleteCharAt(sb.length()-1);
                if(!sb.isEmpty()) {
                    String numb = sb.toString();


                    if(numb.contains(".")) tmp.add(Double.parseDouble(numb));
                    else tmp.add(Integer.parseInt(numb));
                }
            }

            if (!tmp.isEmpty()) {
                result.add(tmp);
            }
        }
    }


    public static List<String> getAllDotaHeroNames() {
        List<String> heroNames = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("https://dota2.fandom.com/wiki/Table_of_hero_attributes").get();

            doc.selectFirst("table[style='width:100%; text-align:center; font-size:88%;']")
                    .selectFirst("tbody")
                    .select("tr")
                    .select("td[style=\"text-align:left;\"]")
                    .forEach(element -> heroNames.add(element.text()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return heroNames;
    }

    public static void fillHeroStats(Hero hero, String name){
        hero.heroName = name;
        try {
            String url = "https://dota2.fandom.com/wiki/" + name;
            Document doc = Jsoup.connect(url).get();

            setMainAttribute(hero,doc);
            setIsMelee(hero,doc);
            setAttributesValues(hero,doc);
            setMagicResistance(hero,doc);
            setDamageAtLevel(hero,doc);
            setAttackSpeed(hero,doc);
            setBaseAttackTime(hero,doc);
            setArmor(hero,doc);
            setHpAndHpRegen(hero,doc);
            setManaAndManaRegen(hero,doc);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setManaAndManaRegen(Hero hero, Document doc) {
        Element manaElement = doc.selectFirst("td[style=\"background:linear-gradient(to right, #318AE7, #41A3EC); font-size:90%; font-weight:bold; color:#fff; text-shadow:1px 1px 2px #000;\"]");

        hero.baseMana = Integer.parseInt(manaElement.text());

        Element manaRegenElement = doc.selectFirst("td[style=\"background:linear-gradient(to right, #318AE7, #41A3EC); color:#204F1C; font-size:80%; font-weight:bold;\"]");

        hero.baseManaRegen = Double.parseDouble(manaRegenElement.text());
    }

    private static void setHpAndHpRegen(Hero hero, Document doc) {
        Element hpElement = doc.selectFirst("td[style=\"background:linear-gradient(to right, #4A9E2E, #59B832); font-size:90%; font-weight:bold; color:#fff; text-shadow:1px 1px 2px #000; border-top:2px solid black;\"]");

        hero.baseHp = Integer.parseInt(hpElement.text());

        Element hpRegenElement = doc.selectFirst("td[style=\"background:linear-gradient(to right, #4A9E2E, #59B832); color:#204F1C; font-size:80%; font-weight:bold;\"]");

        hero.baseHpRegen = Double.parseDouble(hpRegenElement.text());
    }

    private static void setArmor(Hero hero, Document doc) {
        Elements armourElement = doc.select("td[style=\"font-size:85%; border-top:2px solid black;\"]");

        hero.baseArmor = Double.parseDouble(armourElement.get(1).text());
    }

    private static void setBaseAttackTime(Hero hero, Document doc) {
        Element baseAttackTime = doc.selectFirst("td[style=\"width:96px; font-size:90%; text-align:left;\"]");

        String baseAtckTimeStr = baseAttackTime.text();

        hero.BAT = Double.parseDouble(baseAtckTimeStr.substring(1,baseAtckTimeStr.indexOf("s")));
    }

    private static void setAttackSpeed(Hero hero, Document doc) {
        Element attackSpeed = doc.selectFirst("td[style=\"width:48px; font-size:90%;\"]");
        hero.baseAttackSpeed = Integer.parseInt(attackSpeed.text());
    }

    private static void setDamageAtLevel(Hero hero, Document doc) {
        Elements damageAtLevel = doc.select("td[style=\"font-size:80%; border-top:2px solid black;\"]");

        // Theres set damage with levels 0/1/15/25/30
        String[] damageAtLevelTxt = damageAtLevel.get(1).text().split(" ");

        hero.baseDamageLow = Integer.parseInt(damageAtLevelTxt[0]);
        hero.baseDamageHigh = Integer.parseInt(damageAtLevelTxt[1]);
    }

    private static void setMagicResistance(Hero hero, Document doc) {
        Elements magicResistanceAtLevel1 = doc.select("*[style=\"font-size:80%;\"]");

        // Theres set magical resistance with levels 0/1/15/25/30
        String magicResStr = magicResistanceAtLevel1.get(1).text();

        hero.baseMagicResistance = Double.parseDouble(magicResStr.substring(0,magicResStr.indexOf("%")));
    }

    private static void setAttributesValues(Hero hero, Document doc) {
        Elements attributeDiv = doc.select("div[style=\"color:#fff; text-shadow:1px 1px 2px #000;\"]");

        String[][] attributes = new String[3][2];

        int i=0;

        for(Element elements:attributeDiv){
            attributes[i][0] = elements.text().split(" ")[0];
            attributes[i][1] = elements.text().split(" ")[2];
            i++;
            if(i >= attributes.length) break;
        }

        hero.baseStrengthPoints = Integer.parseInt(attributes[0][0]);
        hero.strengthGainPerLevel = Double.parseDouble(attributes[0][1]);
        hero.baseAgilityPoints = Integer.parseInt(attributes[1][0]);
        hero.agilityGainPerLevel = Double.parseDouble(attributes[1][1]);
        hero.baseIntelligencePoints = Integer.parseInt(attributes[2][0]);
        hero.intelligenceGainPerLevel = Double.parseDouble(attributes[2][1]);
    }

    private static void setIsMelee(Hero hero, Document doc) {
        Element atckRange = doc.selectFirst("a[href=\"/wiki/Attack_range#Melee_and_ranged\"]");
        String title = atckRange.attr("title");


        boolean isMelee = title.equals("Melee");
        hero.isMelee = isMelee;

        if(isMelee){
            String naturalDamageBlockValue = doc.select("a[href=\"/wiki/Damage_Block\"]").get(1).text();

            hero.naturalDamageBlock = Integer.parseInt(naturalDamageBlockValue);

            String naturalDamageBlockPercentageStr = doc.selectFirst("span[title=\"Proc Chance\"]").text();

            hero.naturalDamageBlockPercentage = Integer.parseInt(
                    naturalDamageBlockPercentageStr.substring(
                            0,
                            naturalDamageBlockPercentageStr.indexOf("%")
                    ));
        }


    }

    private static void setMainAttribute(Hero hero, Document doc) {
        Element element = doc.selectFirst("[data-tracking-label=categories-top-more-2]");
        String mainAttribute = element.text().split(" ")[0];
        switch (mainAttribute) {
            case "Strength" -> hero.mainAttribute = MainAttribute.STRENGTH;
            case "Agility" -> hero.mainAttribute = MainAttribute.AGILITY;
            case "Intelligence" -> hero.mainAttribute = MainAttribute.INTELLIGENCE;
            case "Universal" -> hero.mainAttribute = MainAttribute.UNIVERSAL;
        }
    }


    public static int getMaximumPossibleHeroLevel() {
        try {
            String url = "https://dota2.fandom.com/wiki/Experience#:~:text=Heroes%20can%20gain%20a%20total,level%20a%20hero%20can%20reach.";
            Document doc = Jsoup.connect(url).get();

            Elements table = doc.select("table[class=\"wikitable mw-datatable\"]");

            Elements tableRows = table.select("tbody").select("tr");

            return tableRows.size() - 1 ; // Minus one because header takes 1 space
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAllItems() {
        List<String> items = new ArrayList<>();

        try {
            String url = "https://dota2.fandom.com/wiki/Items";
            Document doc = Jsoup.connect(url).get();

            Elements itemsClass = doc.select("div[class=\"itemlist\"]");

            List<Element> listElements = itemsClass.subList(0, 11);

            listElements.forEach(el -> extractText(el.text() , items));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return items;
    }

    private static void extractText(String input,List<String> itemsStr) {
        Pattern pattern = Pattern.compile("[a-zA-Z'\\s]+");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String text = matcher.group().trim();
            if(!text.isBlank()){
                itemsStr.add(text);
            }
        }

    }

    private static final String[] PASSIVE_KEYWORDS = {
            "Strength",
            "Agility",
            "Intelligence",
            "Attack Speed",
            "Attack Damage",
            "Health Regeneration",
            "Mana Regeneration",
            "Armor",
            "Health",
            "Mana",
            "Evasion",
            "Magic Resistance"
    };







}