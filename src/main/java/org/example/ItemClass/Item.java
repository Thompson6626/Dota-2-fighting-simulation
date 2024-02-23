package org.example.ItemClass;

import lombok.Data;
import org.example.ItemClass.PassiveEffects.PassiveEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Item {

    public String name;

    public Map<String,Double> mapValues;

    // A map because some items dont have levels
    // The length of any list is the maximum level the specific item can get
    public Map<String, List<? extends Number>> bonusesOnLevel;
    public Integer maxLevel;

    public PassiveEffect passiveEffect;

    public Item(String name){
        this.name = name;
        this.mapValues = new HashMap<>();
    }



}
