package org.pancakelab.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.NullOrder;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.CustomPancake;
import org.pancakelab.model.pancakes.PancakeBuilder;
import org.pancakelab.model.pancakes.PancakeRecipe;

public class PancakeService {
	private Map<UUID, Order> orders = new HashMap<UUID, Order>();
    
    public static String INGREDIENT_DARK_CHOCOLATE = "dark chocolate";
    public static String INGREDIENT_MILK_CHOCOLATE = "milk chocolate";
    public static String INGREDIENT_WHIPPED_CREAM = "whipped cream";
    public static String INGREDIENT_HAZELNUTS = "hazelnuts";
    
    private static final Set<String> APPROVED_INGREDIENTS = new HashSet<>(Set.of(
    	INGREDIENT_DARK_CHOCOLATE, 
    	INGREDIENT_MILK_CHOCOLATE, 
    	INGREDIENT_WHIPPED_CREAM, 
    	INGREDIENT_HAZELNUTS
    ));
    
    public static final int MAX_PANCAKE_COUNT = 100;
    
    private Order getOrder (UUID orderId) {
    	return orders.getOrDefault(orderId, NullOrder.getInstance());
    }

    /**
     * Create an order 
     * @param building
     * @param room
     * @return UUID of order that was created
     */
    public UUID createOrder(int building, int room) {
        Order order = new Order(building, room);
        orders.put(order.getId(), order);
        return order.getId();
    }
    

    public void addDarkChocolatePancake(UUID orderId, int count) {
    	addPancakes(orderId, List.of(INGREDIENT_DARK_CHOCOLATE), count);
    }

    public void addDarkChocolateWhippedCreamPancake(UUID orderId, int count) {
    	addPancakes(orderId, List.of(INGREDIENT_DARK_CHOCOLATE, INGREDIENT_WHIPPED_CREAM), count);
    }

    public void addDarkChocolateWhippedCreamHazelnutsPancake(UUID orderId, int count) {
    	addPancakes(orderId, List.of(INGREDIENT_DARK_CHOCOLATE, INGREDIENT_WHIPPED_CREAM, INGREDIENT_HAZELNUTS), count);
    }

    public void addMilkChocolatePancake(UUID orderId, int count) {
    	addPancakes(orderId, List.of(INGREDIENT_MILK_CHOCOLATE), count);
    }

    public void addMilkChocolateHazelnutsPancake(UUID orderId, int count) {
    	addPancakes(orderId, List.of(INGREDIENT_MILK_CHOCOLATE, INGREDIENT_HAZELNUTS), count);
    }
    
    
    /**
     * Adds a specified number of pancakes to an order
     * Assumption: Pancakes can only be added if not completed
     *
     * @param orderId   The ID of the order to add pancakes to.
     * @param ingredients List of requested ingredients.
     * @param count The number of pancakes to add (capped at {@code MAX_PANCAKE_COUNT}).
     */
    public void addPancakes(UUID orderId, List<String> ingredients, int count) {
    	Order order = getOrder(orderId);

        for (int i = 0; i < Math.min(count, MAX_PANCAKE_COUNT); i++) {
        	PancakeBuilder builder = new PancakeBuilder();
                
            for (String ingredient : getApprovedIngredients(ingredients)) {
            	builder.addIngredient(ingredient);            
            }
            CustomPancake customPancake = builder.build();
            customPancake.setOrderId(orderId);
            order.addPancake(customPancake);
            OrderLog.logAddPancake(order, customPancake.description(), order.getPancakes().size());
        }
    }

    /**
     * Removes specified pancakes from an order. If the order is completed, no pancakes
     * are removed.
     * 
     * Assumption: pancakes can be removed only from orders in initial state
     *
     * @param description The description of the pancake type to remove.
     * @param orderId The ID of the order.
     * @param count The number of pancakes to remove.
     */
    public void removePancakes(String description, UUID orderId, int count) {
    	Order order = getOrder(orderId);
        int countRemoved = 0;
        for (int i = 0; i < count; i++) {
        	if (order.removePancake(description)) {
        		countRemoved++;
        	};
        }

        OrderLog.logRemovePancakes(order, description, countRemoved, order.getPancakes().size());
    }
    
    /**
     * Retrieves a list of pancake descriptions in an order.
     *
     * @param orderId The ID of the order.
     * @return A list of descriptions of pancakes in the order.
     */
	public List<String> viewOrder(UUID orderId) {
		return getOrder(orderId)
				.getPancakes().stream()
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
        Order order = getOrder(orderId);
        removeOrder(order);
        OrderLog.logCancelOrder(order,order.getPancakes().size());
    }

    /**
     * Marks an order as completed.
     *
     * @param orderId The ID of the order to complete.
     */
    public void completeOrder(UUID orderId) {
    	getOrder(orderId).completed();
    }

    /**
     * Returns a set of completed orders.
     *
     * @return A set containing IDs of completed orders.
     */
    public Set<UUID> listCompletedOrders() {
        return orders.values().stream()
        		.filter(order -> order.isCompleted())
        		.map(order -> order.getId())
        		.collect(Collectors.toSet());
    }

    /**
     * Marks an order as prepared.
     *
     * @param orderId The ID of the order to prepare.
     */
    public void prepareOrder(UUID orderId) {
    	getOrder(orderId).prepared();
    }

    /**
     * Returns a set of prepared orders.
     *
     * @return A set containing IDs of prepared orders.
     */
    public Set<UUID> listPreparedOrders() {
        return orders.values().stream()
        		.filter(order -> order.isPrepared())
        		.map(order -> order.getId())
        		.collect(Collectors.toSet());
    }

    /**
     * Delivers an order and removes it from the system.
     *
     * @param orderId The ID of the order to deliver.
     * @return DeliveryResult
     */
    public DeliveryResult deliverOrder(UUID orderId) {
    	Order order = getOrder(orderId);

        List<String> pancakesToDeliver = order.getPancakesToDeliver().stream()
        		.map(PancakeRecipe::description).toList();
        OrderLog.logDeliverOrder(order, order.getPancakes().size());

        if (order.isPrepared()) {
        	removeOrder(order);
        }
        
        return new DeliveryResult(order.isPrepared(), order.getId(), pancakesToDeliver);
    }
    
    /**
     * Remove order helper method
     * 
     * @param Order to remove
     */
    private void removeOrder(Order order) {
        orders.remove(order.getId());
    }
    
    /**
     * Filters and returns only approved ingredients, converted to lowercase.
     */
    private List<String> getApprovedIngredients(List<String> ingredients) {
        return ingredients.stream()
                .map(String::toLowerCase)  
                .filter(APPROVED_INGREDIENTS::contains) 
                .toList();
    }
    
    /**
     * Make sense to provide users with available ingredients
     * @return List of ingredients
     */
    public List<String> getAvailableIngredients() {
    	return new LinkedList<String>(APPROVED_INGREDIENTS);
    }
}
