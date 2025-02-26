package org.pancakelab.model.pancakes;

public interface PancakeBuilder {
	
	PancakeBuilder addIngredient(String ingredient);
	PancakeRecipe build();
}
