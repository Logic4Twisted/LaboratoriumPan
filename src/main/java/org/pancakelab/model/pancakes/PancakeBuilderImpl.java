package org.pancakelab.model.pancakes;

import java.util.LinkedList;
import java.util.List;

public class PancakeBuilderImpl implements PancakeBuilder {
	
	private final List<String> ingredients = new LinkedList<String>();

	@Override
	public PancakeBuilder addIngredient(String ingredient) throws Exception {
		this.ingredients.add(ingredient.toLowerCase());
        return this;
	}

	@Override
	public PancakeRecipe build() throws Exception {
		return new Pancake(this.ingredients);
	}
}
