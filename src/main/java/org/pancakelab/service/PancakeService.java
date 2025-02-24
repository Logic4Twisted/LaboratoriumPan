package org.pancakelab.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.NullOrder;
import org.pancakelab.model.OrderInterface;
import org.pancakelab.model.pancakes.OrderRepository;

public class PancakeService {
	private final OrderRepository orderRepository;
	private final PancakeManager pancakeManager;
	private final OrderFactory orderFactory;
    
    public PancakeService(OrderRepository orderRepository, PancakeManager pancakeManager, OrderFactory orderFactory) {
		this.orderRepository = orderRepository;
		this.pancakeManager = pancakeManager;
		this.orderFactory = orderFactory;
	}
    
    private OrderInterface getOrder (UUID orderId) {
    	return orderRepository.findById(orderId).orElse(NullOrder.getInstance());
    }

    /**
     * Create an order 
     * @param building
     * @param room
     * @return UUID of order that was created
     */
    public UUID createOrder(int building, int room) {
        OrderInterface order = orderFactory.createOrder(building, room);
        order.saveTo(orderRepository);
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
    	OrderInterface order = getOrder(orderId);
    	pancakeManager.addPancakes(order, ingredients, count);
    	order.saveTo(orderRepository);
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
    	OrderInterface order = getOrder(orderId);
    	pancakeManager.removePancakes(order, description, count);
    	order.saveTo(orderRepository);
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
    	OrderInterface order = getOrder(orderId);
        order.cancel();
        order.delete(orderRepository);
    }

    /**
     * Marks an order as completed.
     *
     * @param orderId The ID of the order to complete.
     */
    public void completeOrder(UUID orderId) {
    	OrderInterface order = getOrder(orderId);
    	order.completed();
    	order.saveTo(orderRepository);
    }

    /**
     * Returns a set of completed orders.
     *
     * @return A set containing IDs of completed orders.
     */
    public Set<UUID> listCompletedOrders() {
    	return orderRepository.findAll().stream()
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
    	OrderInterface order = getOrder(orderId);
    	order.prepared();
    	order.saveTo(orderRepository);
    }

    /**
     * Returns a set of prepared orders.
     *
     * @return A set containing IDs of prepared orders.
     */
	public Set<UUID> listPreparedOrders() {
		return orderRepository.findAll().stream()
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
    	OrderInterface order = getOrder(orderId);
    	order.delivered();
    	if (order.isDelivered()) {
    		order.delete(orderRepository);
    	}

        return new DeliveryResult(order.isDelivered(), order.getId(),  order.getPancakesToDeliver());
    }
}
