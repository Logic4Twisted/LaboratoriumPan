package org.pancakelab.model.pancakes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.pancakelab.model.ApprovedIngredients;

public class Pancake implements PancakeRecipe {
	List<String> ingredients;
	
	public Pancake(List<String> ingredients) throws Exception {
		this();
		for (String ingredient : ingredients) {
			addIngredient(ingredient);
		}
	}
	
	public Pancake() {
		this.ingredients = new LinkedList<String>();
	}

	@Override
	public List<String> getIngredients() {
		return ingredients.stream().toList();
	}
	
	public void addIngredient(String ingredient) throws Exception {
		// validating ingredients
		if (!ApprovedIngredients.isApproved(ingredient)) {
			throw new Exception("Ingredient invalid value");
		}
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
		return this.getIngredients().stream().sorted().toList()
	            .equals(other.getIngredients().stream().sorted().toList());
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		for (String ingredient: ingredients) {
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
}
