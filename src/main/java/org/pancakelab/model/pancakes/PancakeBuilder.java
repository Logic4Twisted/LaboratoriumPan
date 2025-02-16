package org.pancakelab.model.pancakes;

public class PancakeBuilder {
	CustomPancake pancake;
	public PancakeBuilder() {
        pancake = new CustomPancake();
    }

    public PancakeBuilder addIngredient(String ingredient) {
    	this.pancake.addIngredient(ingredient);
    	return this;
    }

    public CustomPancake build() {
        return pancake;
    }
}
