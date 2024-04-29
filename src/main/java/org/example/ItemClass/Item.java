package org.example.ItemClass;


import java.util.*;

public class Item {
    public String name;
    public List<Map<String,Object>> bonuses;
    public Item(String name,List<Map<String,Object>> bonuses){
        this.name = name;
        this.bonuses = bonuses;
    }
    public Item(){
        this.name = "";
        this.bonuses = new ArrayList<>();
    }

}
