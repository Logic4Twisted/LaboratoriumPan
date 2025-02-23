package org.pancakelab.model.pancakes;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.pancakelab.model.ApprovedIngredients;
import org.pancakelab.model.Ingredient;

public class Pancake implements PancakeRecipe {
	List<Ingredient> ingredients;
	
	public Pancake(List<String> ingredients) {
		if (ingredients != null) {
			this.ingredients = new LinkedList<Ingredient>(convertToIngredients(getApprovedIngredients(ingredients)));
		} else {
			this.ingredients = new LinkedList<Ingredient>();
		}
	}
	
	public Pancake() {
		this.ingredients = new LinkedList<Ingredient>();
	}

	@Override
	public List<String> getIngredients() {
		return ingredients.stream().map(i -> i.getName()).toList();
	}
	
	public void addIngredient(String ingredient) {
		this.ingredients.addAll(convertToIngredients(getApprovedIngredients(List.of(ingredient))));
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
		return this.getIngredients().stream().sorted().toList()
	            .equals(other.getIngredients().stream().sorted().toList());
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		for (Ingredient ingredient: ingredients) {
			hashCode += ingredient.hashCode();
		}
		return hashCode;
	}
    
    /**
     * Filters and returns only approved ingredients, converted to lowercase.
     */
    private List<String> getApprovedIngredients(List<String> ingredients) {
        return  Optional.ofNullable(ingredients)
        	    .orElse(Collections.emptyList())
        	    .stream()
                .map(String::toLowerCase)  
                .filter(ApprovedIngredients.getAll()::contains) 
                .toList();
    }
    
    /**
     * Make sense to provide users with available ingredients
     * @return List of ingredients
     */
    public List<String> getAvailableIngredients() {
    	return new LinkedList<String>(ApprovedIngredients.getAll());
    }
    
    private List<Ingredient> convertToIngredients(List<String> ingredientNames) {
        return ingredientNames.stream()
                .map(Ingredient::new)
                .toList();
    }
}
