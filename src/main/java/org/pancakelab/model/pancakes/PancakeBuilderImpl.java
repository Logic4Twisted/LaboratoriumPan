package org.pancakelab.model.pancakes;

import java.util.LinkedList;
import java.util.List;

import org.pancakelab.model.ApprovedIngredients;

public class PancakeBuilderImpl implements PancakeBuilder {
	
	private final List<String> ingredients = new LinkedList<String>();

	@Override
	public PancakeBuilder addIngredient(String ingredient) throws Exception {
		if (ingredient == null || !ApprovedIngredients.isApproved(ingredient.toLowerCase())) {
			throw new Exception("Ingredient invalid value");
		}
		this.ingredients.add(ingredient.toLowerCase());
        return this;
	}

	@Override
	public PancakeRecipe build() {
		return new Pancake(this.ingredients);
	}
}
