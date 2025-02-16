package org.pancakelab.model;

import java.util.List;

public class DeliveryResult {
    private final boolean success;
    private final Order order;
    private final List<String> pancakesToDeliver;

    public DeliveryResult(boolean success, Order order, List<String> pancakesToDeliver) {
        this.success = success;
        this.order = order;
        this.pancakesToDeliver = pancakesToDeliver;
    }

    public boolean isSuccess() {
        return success;
    }

    public Order getOrder() {
        return order;
    }

    public List<String> getPancakesToDeliver() {
        return pancakesToDeliver;
    }

    @Override
    public String toString() {
        return "DeliveryResult{" +
                "success=" + success +
                ", order=" + order +
                ", pancakesToDeliver=" + pancakesToDeliver +
                '}';
    }
}