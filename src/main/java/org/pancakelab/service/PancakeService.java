package org.pancakelab.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.CustomPancake;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.PancakeBuilder;
import org.pancakelab.model.pancakes.PancakeRecipe;

public class PancakeService {
	private Map<UUID, Order> mapOrders = new HashMap<UUID, Order>();
    private Set<UUID>           completedOrders = new HashSet<>();
    private Set<UUID>           preparedOrders  = new HashSet<>();
    private List<PancakeRecipe> pancakes        = new ArrayList<>();
    
    private static final Set<String> APPROVED_INGREDIENTS = new HashSet<>(Set.of(
    	"dark chocolate", 
    	"milk chocolate", 
    	"whipped cream", 
    	"hazelnuts"
    ));
    
    
    private Optional<Order> getOrder(UUID orderId) {
    	return Optional.ofNullable(mapOrders.get(orderId));
    }
    
    private boolean orderExists(UUID orderId) {
    	return getOrder(orderId).isPresent();
    }
    
    private boolean isOrderCompleted(UUID orderId) {
    	return completedOrders.contains(orderId) || isOrderPrepared(orderId);
    }
    
    private boolean isOrderPrepared(UUID orderId) {
    	return preparedOrders.contains(orderId);
    }

    /**
     * Create an order 
     * @param building
     * @param room
     * @return UUID of order that was created
     */
    public UUID createOrder(int building, int room) {
        Order order = new Order(building, room);
        mapOrders.put(order.getId(), order);
        return order.getId();
    }
    

    public void addDarkChocolatePancake(UUID orderId, int count) {
    	addCustomPancake(orderId, List.of("dark chocolate"), count);
    }

    public void addDarkChocolateWhippedCreamPancake(UUID orderId, int count) {
    	addCustomPancake(orderId, List.of("dark chocolate", "whipped cream"), count);
    }

    public void addDarkChocolateWhippedCreamHazelnutsPancake(UUID orderId, int count) {
    	addCustomPancake(orderId, List.of("dark chocolate", "whipped cream", "hazelnuts"), count);
    }

    public void addMilkChocolatePancake(UUID orderId, int count) {
    	addCustomPancake(orderId, List.of("milk chocolate"), count);
    }

    public void addMilkChocolateHazelnutsPancake(UUID orderId, int count) {
    	addCustomPancake(orderId, List.of("milk chocolate", "hazelnuts"), count);
    }
    
    
    private void addCustomPancake(UUID orderId, List<String> ingredients, int count) {
        if (!orderExists(orderId) || isOrderCompleted(orderId)) {
            return;
        }

        List<CustomPancake> pancakesToAdd = new ArrayList<>();
        for (int i = 0; i < count; i++) {
        	PancakeBuilder builder = new PancakeBuilder();
                
            for (String ingredient : getApprovedIngredients(ingredients)) {
            	builder.addIngredient(ingredient);            
            }
            CustomPancake customPancake = builder.build();
            customPancake.setOrderId(orderId);
            pancakesToAdd.add(customPancake);
        }
        addPancake(pancakesToAdd, orderId);
    }
    
    /**
     * Filters and returns only approved ingredients, converted to lowercase.
     */
    private List<String> getApprovedIngredients(List<String> ingredients) {
        return ingredients.stream()
                .map(String::toLowerCase)  // Normalize case first
                .filter(APPROVED_INGREDIENTS::contains) // Check against set
                .toList(); // Returns immutable list
    }
    
    
    /**
     * Make sense to provide users with available ingredients
     * @return List of ingredients
     */
    public List<String> getAvailableIngredients() {
    	return new LinkedList<String>(APPROVED_INGREDIENTS);
    }


    /**
     * Adds a collection of pancakes to a specified order.
     * 
     * This method associates each pancake in the given collection with the specified order,
     * updates its order ID, and logs the addition. If the order does not exist or order
     * already completed, no pancakes are added.
     *
     * @param pancakesToAdd The collection of {@link Pancake} objects to be added.
     * @param orderId The UUID of the order to which the pancakes should be assigned.
     */
    private void addPancake(Collection<CustomPancake> pancakesToAdd, UUID orderId) {
    	getOrder(orderId).ifPresent(order -> {
    		if (isOrderCompleted(orderId)) {
    			return;
    		}
    		for (PancakeRecipe pancakeRecipe : pancakesToAdd) {
    			pancakeRecipe.setOrderId(orderId);
            	pancakes.add(pancakeRecipe);
            	OrderLog.logAddPancake(order, pancakeRecipe.description(), viewOrder(orderId).size());
    		}
    	});
    }

    /**
     * Removes specified pancakes from an order. If the order is completed, no pancakes
     * are removed
     *
     * @param description The description of the pancake type to remove.
     * @param orderId The ID of the order.
     * @param count The number of pancakes to remove.
     */
    public void removePancakes(String description, UUID orderId, int count) {
    	if (!orderExists(orderId) || isOrderCompleted(orderId)) {
			return;
		}
        final AtomicInteger removedCount = new AtomicInteger(0);
        pancakes.removeIf(pancake -> {
            return pancake.getOrderId().equals(orderId) &&
                   pancake.description().equals(description) &&
                   removedCount.getAndIncrement() < count;
        });

        Optional<Order> optionalOrder = getOrder(orderId);
        if (optionalOrder.isPresent()) {
        	Order order = optionalOrder.get();
        	OrderLog.logRemovePancakes(order, description, removedCount.get(), viewOrder(orderId).size());
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
     * Cancels an order and removes all its pancakes.
     * 
     * changed behaviour: if order does not exists it does not throw an exception 
     *
     * @param orderId The ID of the order to cancel.
     */
    public void cancelOrder(UUID orderId) {
    	Optional<Order> optionalOrder = getOrder(orderId);
    	if (optionalOrder.isEmpty()) {
    		OrderLog.logNotExistingOrder(orderId);
    		return;
    	}
        Order order = optionalOrder.get();
        long pancakesInOrder = viewOrder(orderId).size();
        removeOrder(orderId);

        OrderLog.logCancelOrder(order,pancakesInOrder);
    }

    /**
     * Marks an order as completed.
     *
     * @param orderId The ID of the order to complete.
     */
    public void completeOrder(UUID orderId) {
    	if (!orderExists(orderId)) {
    		OrderLog.logNotExistingOrder(orderId);
    		return;
    	}
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
    	if (!orderExists(orderId)) {
    		OrderLog.logNotExistingOrder(orderId);
    		return;
    	}
    	if (!completedOrders.contains(orderId)) {
    		OrderLog.LogNotCompletedOrder(orderId);
    		return;
    	}
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
     * @return DeliveryResult
     */
    public DeliveryResult deliverOrder(UUID orderId) {
        if (!isOrderPrepared(orderId)) {
        	return new DeliveryResult(false, null, List.of());
        }

        Order order = getOrder(orderId).get();
        List<String> pancakesToDeliver = viewOrder(orderId);
        OrderLog.logDeliverOrder(order, pancakesToDeliver.size());

        removeOrder(orderId);
        
        return new DeliveryResult(true, order.getId(), pancakesToDeliver);
    }
    
    /**
     * Remove order helper method
     * 
     * @param orderId The ID of the order to remove
     */
    private void removeOrder(UUID orderId) {
        pancakes.removeIf(pancake -> pancake.getOrderId().equals(orderId));
        mapOrders.remove(orderId);
        completedOrders.remove(orderId);
        preparedOrders.remove(orderId);
    }
}
