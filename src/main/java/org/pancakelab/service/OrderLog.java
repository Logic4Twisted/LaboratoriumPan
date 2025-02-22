package org.pancakelab.service;

import org.pancakelab.model.Order;
import java.util.UUID;

public class OrderLog {
    private static final StringBuilder log = new StringBuilder();

    public static void logAddPancake(Order order, String description, long pancakesInOrder) {
        log.append("Added pancake with description '%s' ".formatted(description))
           .append("to order %s containing %d pancakes, ".formatted(order.getId(), pancakesInOrder))
           .append("for building %d, room %d.".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logRemovePancakes(Order order, String description, int count, long pancakesInOrder) {
        log.append("Removed %d pancake(s) with description '%s' ".formatted(count, description))
           .append("from order %s now containing %d pancakes, ".formatted(order.getId(), pancakesInOrder))
           .append("for building %d, room %d.".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logCancelOrder(Order order, long pancakesInOrder) {
        log.append("Cancelled order %s with %d pancakes ".formatted(order.getId(), pancakesInOrder))
           .append("for building %d, room %d.".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logDeliverOrder(Order order, long pancakesInOrder) {
        log.append("Order %s with %d pancakes ".formatted(order.getId(), pancakesInOrder))
           .append("for building %d, room %d out for delivery.".formatted(order.getBuilding(), order.getRoom()));
    }
    
    public static void logNotExistingOrder(UUID orderId) {
    	log.append("Order %s does not exist".formatted(orderId.toString()));
    }
    
    public static void LogNotCompletedOrder(UUID orderId) {
    	log.append("Order %s is not completed yet".formatted(orderId.toString()));
    }
    
    public static void logUnknownIngredient(String ingredient) {
    	log.append("Unknown ingredient: " + ingredient);
    }
}
