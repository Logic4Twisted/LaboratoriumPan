package org.pancakelab.model.pancakes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Pancake implements PancakeRecipe {
	UUID orderId;
	List<String> ingredients;
	
	public Pancake(UUID orderID) {
		this();
		this.orderId = orderID;
	}
	
	public Pancake() {
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
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		Pancake other = (Pancake) obj;
		Collections.sort(other.ingredients());
		Collections.sort(this.ingredients);
		return this.ingredients.equals(other.ingredients());
	}
}
