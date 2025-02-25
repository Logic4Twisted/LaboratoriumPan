package org.pancakelab.model;

import java.util.List;
import java.util.UUID;

public class ViewOrderResult {

	public ViewOrderResult(boolean success, UUID orderId, List<String> pancakes) {
		super();
		this.success = success;
		this.pancakes = pancakes;
		this.orderId = orderId;
	}
	private final boolean success;
    private final List<String> pancakes;
    private final UUID orderId;
    
    public boolean isSuccess() {
		return success;
	}
	public List<String> getPancakes() {
		return pancakes;
	}
	public UUID getOrderId() {
		return orderId;
	}
}
