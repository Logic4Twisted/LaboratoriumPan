package org.pancakelab.model.pancakes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Pancake implements PancakeRecipe {
	List<String> ingredients;
	
	public Pancake(List<String> ingredients) {
		this.ingredients = new LinkedList<String>(ingredients);
	}
	
	public Pancake() {
		this.ingredients = new LinkedList<String>();
	}

	@Override
	public List<String> getIngredients() {
		return List.copyOf(ingredients);
	}
	
	public void addIngredient(String ingredient) {
		this.ingredients.add(ingredient);
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
}
