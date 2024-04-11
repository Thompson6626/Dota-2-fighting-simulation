package org.example.ItemClass;


import java.util.*;

public class Item {
    public String name;
    public Map<String,Double> mapValues;

    // A map because some items dont have levels
    // The length of any list is the maximum level the specific item can get
    public Map<String, List<? extends Number>> bonusesOnLevel;
    public Integer maxLevel;
    //public Buffes buffes = null;

    public Item(String name){
        this.name = name;
        this.mapValues = new HashMap<>();
    }



}
