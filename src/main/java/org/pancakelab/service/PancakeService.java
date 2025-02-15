package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PancakeService {
    private List<Order>         orders          = new ArrayList<>();
    private Set<UUID>           completedOrders = new HashSet<>();
    private Set<UUID>           preparedOrders  = new HashSet<>();
    private List<PancakeRecipe> pancakes        = new ArrayList<>();

    public Order createOrder(int building, int room) {
        Order order = new Order(building, room);
        orders.add(order);
        return order;
    }

    public void addDarkChocolatePancake(UUID orderId, int count) {
        for (int i = 0; i < count; ++i) {
            addPancake(new DarkChocolatePancake(),
                       orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get());
        }
    }

    public void addDarkChocolateWhippedCreamPancake(UUID orderId, int count) {
        for (int i = 0; i < count; ++i) {
            addPancake(new DarkChocolateWhippedCreamPancake(),
                       orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get());
        }
    }

    public void addDarkChocolateWhippedCreamHazelnutsPancake(UUID orderId, int count) {
        for (int i = 0; i < count; ++i) {
            addPancake(new DarkChocolateWhippedCreamHazelnutsPancake(),
                       orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get());
        }
    }

    public void addMilkChocolatePancake(UUID orderId, int count) {
        for (int i = 0; i < count; ++i) {
            addPancake(new MilkChocolatePancake(),
                       orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get());
        }
    }

    public void addMilkChocolateHazelnutsPancake(UUID orderId, int count) {
        for (int i = 0; i < count; ++i) {
            addPancake(new MilkChocolateHazelnutsPancake(),
                       orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get());
        }
    }

    /**
     * Retrieves a list of pancake descriptions in an order.
     *
     * @param orderId The ID of the order.
     * @return A list of descriptions of pancakes in the order.
     */
    public List<String> viewOrder(UUID orderId) {
        return pancakes.stream()
                       .filter(pancake -> pancake.getOrderId().equals(orderId))
                       .map(PancakeRecipe::description).toList();
    }


    /**
     * Adds a pancake to a specific order and logs the addition.
     *
     * @param pancake The {@link PancakeRecipe} object representing the pancake to be added.
     * @param order   The {@link Order} to which the pancake will be assigned.
     */
    private void addPancake(PancakeRecipe pancake, Order order) {
        pancake.setOrderId(order.getId());
        pancakes.add(pancake);

        OrderLog.logAddPancake(order, pancake.description(), pancakes);
    }

    /**
     * Removes specified pancakes from an order.
     *
     * @param description The description of the pancake type to remove.
     * @param orderId The ID of the order.
     * @param count The number of pancakes to remove.
     */
    public void removePancakes(String description, UUID orderId, int count) {
        final AtomicInteger removedCount = new AtomicInteger(0);
        pancakes.removeIf(pancake -> {
            return pancake.getOrderId().equals(orderId) &&
                   pancake.description().equals(description) &&
                   removedCount.getAndIncrement() < count;
        });

        Order order = orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get();
        OrderLog.logRemovePancakes(order, description, removedCount.get(), pancakes);
    }

    /**
     * Cancels an order and removes all its pancakes.
     *
     * @param orderId The ID of the order to cancel.
     */
    public void cancelOrder(UUID orderId) {
        Order order = orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get();
        OrderLog.logCancelOrder(order, this.pancakes);

        pancakes.removeIf(pancake -> pancake.getOrderId().equals(orderId));
        orders.removeIf(o -> o.getId().equals(orderId));
        completedOrders.removeIf(u -> u.equals(orderId));
        preparedOrders.removeIf(u -> u.equals(orderId));

        OrderLog.logCancelOrder(order,pancakes);
    }

    /**
     * Marks an order as completed.
     *
     * @param orderId The ID of the order to complete.
     */
    public void completeOrder(UUID orderId) {
        completedOrders.add(orderId);
    }

    /**
     * Returns a set of completed orders.
     *
     * @return A set containing IDs of completed orders.
     */
    public Set<UUID> listCompletedOrders() {
        return completedOrders;
    }

    /**
     * Marks an order as prepared.
     *
     * @param orderId The ID of the order to prepare.
     */
    public void prepareOrder(UUID orderId) {
        preparedOrders.add(orderId);
        completedOrders.removeIf(u -> u.equals(orderId));
    }

    /**
     * Returns a set of prepared orders.
     *
     * @return A set containing IDs of prepared orders.
     */
    public Set<UUID> listPreparedOrders() {
        return preparedOrders;
    }

    /**
     * Delivers an order and removes it from the system.
     *
     * @param orderId The ID of the order to deliver.
     * @return An array where:
     *         - The first element is the delivered {@link Order}.
     *         - The second element is a list of pancake descriptions.
     *         Returns {@code null} if the order was not prepared.
     */
    public Object[] deliverOrder(UUID orderId) {
        if (!preparedOrders.contains(orderId)) return null;

        Order order = orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().get();
        List<String> pancakesToDeliver = viewOrder(orderId);
        OrderLog.logDeliverOrder(order, this.pancakes);

        pancakes.removeIf(pancake -> pancake.getOrderId().equals(orderId));
        orders.removeIf(o -> o.getId().equals(orderId));
        preparedOrders.removeIf(u -> u.equals(orderId));

        return new Object[] {order, pancakesToDeliver};
    }
}
