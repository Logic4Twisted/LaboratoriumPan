package org.pancakelab.model.pancakes;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Pancake implements PancakeRecipe {
	List<String> ingredients;
	
	public Pancake(List<String> ingredients) {
		if (ingredients != null) {
			this.ingredients = new LinkedList<String>(getApprovedIngredients(ingredients));
		} else {
			this.ingredients = new LinkedList<String>();
		}
	}
	
	public Pancake() {
		this.ingredients = new LinkedList<String>();
	}

	@Override
	public List<String> getIngredients() {
		return List.copyOf(ingredients);
	}
	
	public void addIngredient(String ingredient) {
		this.ingredients.addAll(getApprovedIngredients(List.of(ingredient)));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		Pancake other = (Pancake) obj;
		List<String> otherIngredients = new LinkedList<String>(other.getIngredients());
		Collections.sort(otherIngredients);
		Collections.sort(this.ingredients);
		return this.ingredients.equals(otherIngredients);
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		for (String ingredient: ingredients) {
			hashCode += ingredient.hashCode();
		}
		return hashCode;
	}
	
	public static String INGREDIENT_DARK_CHOCOLATE = "dark chocolate";
    public static String INGREDIENT_MILK_CHOCOLATE = "milk chocolate";
    public static String INGREDIENT_WHIPPED_CREAM = "whipped cream";
    public static String INGREDIENT_HAZELNUTS = "hazelnuts";
    
    private static final Set<String> APPROVED_INGREDIENTS = new HashSet<>(Set.of(
    	INGREDIENT_DARK_CHOCOLATE, 
    	INGREDIENT_MILK_CHOCOLATE, 
    	INGREDIENT_WHIPPED_CREAM, 
    	INGREDIENT_HAZELNUTS
    ));
    
    /**
     * Filters and returns only approved ingredients, converted to lowercase.
     */
    private List<String> getApprovedIngredients(List<String> ingredients) {
        return  Optional.ofNullable(ingredients)
        	    .orElse(Collections.emptyList())
        	    .stream()
                .map(String::toLowerCase)  
                .filter(APPROVED_INGREDIENTS::contains) 
                .toList();
    }
    
    /**
     * Make sense to provide users with available ingredients
     * @return List of ingredients
     */
    public List<String> getAvailableIngredients() {
    	return new LinkedList<String>(APPROVED_INGREDIENTS);
    }
}
