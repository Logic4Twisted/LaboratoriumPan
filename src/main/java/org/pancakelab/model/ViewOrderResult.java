package org.pancakelab.model;

import java.util.List;
import java.util.UUID;

public class ViewOrderResult {

	public ViewOrderResult(boolean success, UUID orderId, List<String> pancakes, String message) {
		this.success = success;
		this.pancakes = pancakes;
		this.orderId = orderId;
		this.message = message;
	}
	private final boolean success;
    private final List<String> pancakes;
    private final UUID orderId;
    private final String message;
    
    public boolean isSuccess() {
		return success;
	}
	public List<String> getPancakes() {
		return pancakes;
	}
	public UUID getOrderId() {
		return orderId;
	}
	public String getMessage() {
		return message;
	}
}
