package org.pancakelab.model;

import java.util.Set;

public class ApprovedIngredients {
	public static String INGREDIENT_DARK_CHOCOLATE = "dark chocolate";
	public static String INGREDIENT_MILK_CHOCOLATE = "milk chocolate";
	public static String INGREDIENT_HAZELNUTS = "hazelnuts";
	public static String INGREDIENT_WHIPPED_CREAM = "whipped cream";
	
    private static final Set<String> INGREDIENTS = Set.of(
        "dark chocolate",
        "milk chocolate",
        "whipped cream",
        "hazelnuts"
    );

    public static boolean isApproved(String ingredient) {
        return INGREDIENTS.contains(ingredient.toLowerCase());
    }

    public static Set<String> getAll() {
        return INGREDIENTS;
    }
}