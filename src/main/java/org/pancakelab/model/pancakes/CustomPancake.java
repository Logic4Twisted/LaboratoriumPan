package org.pancakelab.model.pancakes;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CustomPancake implements PancakeRecipe {
	UUID orderId;
	List<String> ingredients;
	
	public CustomPancake(UUID orderID) {
		this();
		this.orderId = orderID;
	}
	
	public CustomPancake() {
		this.ingredients = new LinkedList<String>();
	}
	

	@Override
	public UUID getOrderId() {
		return orderId;
	}

	@Override
	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
		
	}

	@Override
	public List<String> ingredients() {
		return ingredients;
	}
	
	public void addIngredient(String ingredient) {
		this.ingredients.add(ingredient);
	}
}
