package org.pancakelab.model.pancakes;

import java.util.HashMap;
import java.util.Map;

public class PancakeFactory {
	private static final Map<String, Pancake> pancakeCache = new HashMap<>();

    public static Pancake getPancake(String type) {
        return pancakeCache.computeIfAbsent(type, t -> switch (t) {
            case "DarkChocolatePancake" -> new DarkChocolatePancake();
            case "DarkChocolateWhippedCreamPancake" -> new DarkChocolateWhippedCreamPancake();
            case "DarkChocolateWhippedCreamHazelnutsPancake" -> new DarkChocolateWhippedCreamHazelnutsPancake();
            case "MilkChocolateHazelnutsPancake" -> new MilkChocolateHazelnutsPancake();
            case "MilkChocolatePancake" -> new MilkChocolatePancake();
            
            default -> throw new IllegalArgumentException("Unknown pancake type: " + t);
        });
    }

}
