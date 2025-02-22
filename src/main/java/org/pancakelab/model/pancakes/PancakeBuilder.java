package org.pancakelab.model.pancakes;

public class PancakeBuilder {
	Pancake pancake;
	public PancakeBuilder() {
        pancake = new Pancake();
    }

    public PancakeBuilder addIngredient(String ingredient) {
    	this.pancake.addIngredient(ingredient);
    	return this;
    }

    public Pancake build() {
        return pancake;
    }
}
