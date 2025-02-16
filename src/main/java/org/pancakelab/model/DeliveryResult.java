package org.pancakelab.model;

import java.util.List;
import java.util.UUID;

public class DeliveryResult {
    private final boolean success;
    private final UUID orderId;
    private final List<String> pancakesToDeliver;

    public DeliveryResult(boolean success, UUID orderId, List<String> pancakesToDeliver) {
        this.success = success;
        this.orderId = orderId;
        this.pancakesToDeliver = pancakesToDeliver;
    }

    public boolean isSuccess() {
        return success;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public List<String> getPancakesToDeliver() {
        return pancakesToDeliver;
    }

    @Override
    public String toString() {
        return "DeliveryResult{" +
                "success=" + success +
                ", order=" + orderId +
                ", pancakesToDeliver=" + pancakesToDeliver +
                '}';
    }
}