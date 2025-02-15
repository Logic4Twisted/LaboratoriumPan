package org.pancakelab.model.pancakes;

import java.util.List;
import java.util.UUID;

public class Pancake implements PancakeRecipe {
	
	private UUID orderId;
	private PancakeType type;
	
	public Pancake(PancakeType type) {
		this.type = type;
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
		return type.ingredients();
	}
	
	public PancakeType getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Pancake other = (Pancake) obj;
		return other.orderId.equals(other.getOrderId()) && other.getType() == this.getType();
	}

}
