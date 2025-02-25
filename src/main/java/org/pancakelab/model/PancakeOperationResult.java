package org.pancakelab.model;

import java.util.UUID;

public class PancakeOperationResult {
    private final boolean success;
    private final String message;
    private final UUID orderId;

    public PancakeOperationResult(boolean success, UUID orderId, String message) {
        this.success = success;
        this.message = message;
        this.orderId = orderId;
    }
    
    public PancakeOperationResult(boolean success, UUID orderId) {
        this(success, orderId, "");
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
    
    public UUID getOrderId() {
    	return orderId;
    }
}