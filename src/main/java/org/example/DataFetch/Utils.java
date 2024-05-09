package org.example.DataFetch;

import org.example.ItemClass.QuantityWrapper.QuantityValueAttrWrapper;
import org.example.ItemClass.QuantityWrapper.QuantityValueWrapper;
import org.example.ItemClass.QuantityWrapper.QuantityWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {
    private static final Random RANDOM_GENERATOR = new Random();

    public static double sumFromMapOfQuantityWrappers(
            Map<String,Object> valuesMap,
            String keyBuff
    ){
        Map<String , QuantityWrapper> map =
                (Map<String ,QuantityWrapper>) valuesMap.getOrDefault(
                        keyBuff,
                        Collections.emptyMap()
                );

        return map.values().stream()
                .mapToDouble(wrapper -> ((QuantityValueWrapper) wrapper).getValue())
                .sum();
    }
    /**
     If add is true it will create a QuantityValueWrapper if it doesnt exist  or increment the already existing quantity value
     , if add is false it will decrement the quantity of the existing value and if the quantity reaches 0 it will be deleted.

     When a QuantityWrapper is created it will have 1 as default quantity
     */
    public static void updateQuantity(
            String itemName,
            Map<String, QuantityWrapper> map,
            boolean add,
            double value
    ){
        map.compute(itemName, (k, oldValue) ->
                (add ?
                        ((oldValue == null) ? new QuantityValueWrapper(value) : oldValue.incrementQuantity())
                        :
                        ((oldValue != null) ?
                                (oldValue.decrementQuantity().getQuantity() <= 0 ? null : oldValue)
                                :
                                null)
                ));
    }
    public static void updateDebuffToApply(
            Map<String, Map<String, QuantityWrapper>> debuffsToApply,
            String debuffType,
            String name,
            double value,
            boolean add
    ) {
        debuffsToApply.computeIfAbsent(debuffType, e -> new HashMap<>());
        Utils.updateQuantity(
                name,
                debuffsToApply.get(debuffType),
                add,
                value
        );
    }
    public static void updateAttrDependantDebuff(
            Map<String, Map<String, QuantityWrapper>> debuffsToApply,
            String debuffType,
            String keyVal,
            String itemName,
            double value,
            boolean add
    ){
        debuffsToApply.computeIfAbsent(debuffType, e -> new HashMap<>());
        Map<String,QuantityWrapper> debuffTypeMap = debuffsToApply.get(debuffType);
        debuffTypeMap.putIfAbsent(itemName,new QuantityValueAttrWrapper());

        if (add)
            debuffTypeMap.get(itemName).incrementQuantity();
        else
            debuffTypeMap.get(itemName).decrementQuantity();


        if (debuffTypeMap.get(itemName).getQuantity() <= 0){
            debuffTypeMap.remove(itemName);
            return;
        }
        QuantityValueAttrWrapper qVW = (QuantityValueAttrWrapper) debuffTypeMap.get(itemName);
        if (keyVal.contains("melee")){
            qVW.setMeleeValue(value);
        }else if (keyVal.contains("range")){
            qVW.setRangeValue(value);
        }
    }
    public static double calculatePercentage(double value, double percentage){
        return value * percentage / 100;
    }
    public static double roundToDesiredDecimals(double num, int decimalPlaces){
        double digits = Math.pow(10 , decimalPlaces);
        return Math.round(num * digits) / digits;
    }
    public static boolean checkChance(double chance){
        return checkChance(chance ,RANDOM_GENERATOR);
    }
    public static boolean checkChance(double chance,Random random){
        return random.nextInt(100) < chance;
    }
    public static <T> T randomChoice(T[] arr){
        return randomChoice(arr,RANDOM_GENERATOR);
    }

    /**
     * Gets a random item from the given array using the random instance given
     * @param arr Array of choices
     * @param random Random instance given
     * @return A random item from the array
     */
    public static <T> T randomChoice(T[] arr , Random random){
        return arr[random.nextInt(arr.length)];
    }

}
