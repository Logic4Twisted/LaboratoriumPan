package org.pancakelab.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.NullOrder;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.OrderRepository;

public class PancakeService {
	private OrderRepository orderRepository;
    
    public static final int MAX_PANCAKE_COUNT = 100;
    
    public PancakeService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
    
    private Order getOrder (UUID orderId) {
    	return orderRepository.findById(orderId).orElse(NullOrder.getInstance());
    }

    /**
     * Create an order 
     * @param building
     * @param room
     * @return UUID of order that was created
     */
    public UUID createOrder(int building, int room) {
        Order order = new Order(building, room);
        orderRepository.save(order);
        return order.getId();
    }
    
    
    /**
     * Adds a specified number of pancakes to an order
     * Requirements: Pancakes can only be added if order not completed
     * Requirements: Pancake without ingredients is possible
     *
     * @param orderId   The ID of the order to add pancakes to.
     * @param ingredients List of requested ingredients.
     * @param count The number of pancakes to add (capped at {@code MAX_PANCAKE_COUNT}).
     */
    public void addPancakes(UUID orderId, List<String> ingredients, int count) {
    	// basic validation
    	count = Math.min(count, MAX_PANCAKE_COUNT);
    	
    	Order order = getOrder(orderId);
    	for (int i = 0; i < count; i++) {
    		order.addPancake(ingredients);
        }
    }

    /**
     * Removes specified pancakes from an order. If the order is completed, no pancakes
     * are removed.
     * 
     * Requirements: pancakes can be removed only from orders in initial state?
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
            }
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
		return getOrder(orderId).getPancakes();
	}

    /**
     * Cancels an order and removes all its pancakes.
     * 
     * Requirements:
     * Can we cancel order that is completed? 
     * in Readme: "3.The Disciple can choose to complete or cancel the Order, if cancelled the Order is removed from the database."
     * does this means it can only be cancelled before its completed? I chose to allow cancelling anytime before delivery
     * 
     * If the order does not exists it does not throw an exception 
     *
     * @param orderId The ID of the order to cancel.
     */
    public void cancelOrder(UUID orderId) {
        Order order = getOrder(orderId);
        orderRepository.delete(orderId);
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
    	return orderRepository.findAll().parallelStream()
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
		return orderRepository.findAll().parallelStream()
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
    	order.delivered();
    	if (order.isDelivered()) {
    		orderRepository.delete(orderId);
    	}

        OrderLog.logDeliverOrder(order, order.getPancakes().size());
        return new DeliveryResult(order.isDelivered(), order.getId(),  order.getPancakesToDeliver());
    }
}
