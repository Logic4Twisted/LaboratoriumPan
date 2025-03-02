package org.pancakelab.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.NullOrder;
import org.pancakelab.model.OrderInterface;
import org.pancakelab.model.PancakeOperationResult;
import org.pancakelab.model.ViewOrderResult;
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
    public PancakeOperationResult createOrder(int building, int room) {
    	if (building <= 0 || room <= 0) {
            return new PancakeOperationResult(false, null, "Invalid building or room number.");
        }
        OrderInterface order = orderFactory.createOrder(building, room);
        order.updateRepository(orderRepository);
        return new PancakeOperationResult(true, order.getId());
    }
    
    
    /**
     * Adds a specified number of pancakes to an order
     * 
     * Requirements: Pancakes can only be added if order not completed
     * Requirements: Pancake without ingredients is possible
     *
     * @param orderId   The ID of the order to add pancakes to.
     * @param ingredients List of requested ingredients.
     * @param count The number of pancakes to add (capped at {@code MAX_PANCAKE_COUNT}).
     * @return PancakeOperationResult
     */
    public PancakeOperationResult addPancakes(UUID orderId, List<String> ingredients, int count) {
        if (orderId == null) {
            return new PancakeOperationResult(false, orderId, "Order ID cannot be null.");
        }
        if (ingredients == null || ingredients.isEmpty()) {
        	return new PancakeOperationResult(false, orderId, "Ingredient list is null or empty.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new PancakeOperationResult(false, orderId, "Order not found.");
        }
        
        try {
	    	pancakeManager.addPancakes(order, ingredients, count);
	    	order.updateRepository(orderRepository);
	    	return new PancakeOperationResult(true, order.getId());
        } catch (Exception e) {
        	return new PancakeOperationResult(false, order.getId(), e.getMessage());
        }
    }

    /**
     * Removes specified pancakes from an order. If the order is completed, no pancakes
     * are removed.
     * 
     * Requirements: pancakes can be removed only from orders in initial state? (yes)
     * Note: This description is kind of weird
     *
     * @param description The description of the pancake type to remove.
     * @param orderId The ID of the order.
     * @param count The number of pancakes to remove.
     * @return PancakeOperationResult
     */
    public PancakeOperationResult removePancakes(String description, UUID orderId, int count) {
        if (orderId == null) {
            return new PancakeOperationResult(false, orderId, "Order ID cannot be null.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new PancakeOperationResult(false, orderId, "Order not found.");
        }
        
        try {
	    	pancakeManager.removePancakes(order, description, count);
	    	order.updateRepository(orderRepository);
	    	return new PancakeOperationResult(true, order.getId());
        } catch (Exception e) {
        	return new PancakeOperationResult(false, orderId, e.getMessage());
        }
    }
    
    /**
     * Retrieves a list of pancake descriptions in an order.
     *
     * @param orderId The ID of the order.
     * @return ViewOrderResult
     */
	public ViewOrderResult viewOrder(UUID orderId) {
        if (orderId == null) {
            return new ViewOrderResult(false, orderId, List.of(), "Order ID cannot be null.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new ViewOrderResult(false, orderId, List.of(), "Order not found.");
        }
		return new ViewOrderResult(true, orderId, order.getPancakes(), "");
	}

    /**
     * Cancels an order and removes all its pancakes.
     * 
     * Requirements:
     * Can we cancel order that is completed? 
     * in Readme: "3.The Disciple can choose to complete or cancel the Order, if cancelled the Order is removed from the database."
     * does this means it can only be cancelled before its completed? 
     * I chose to allow cancelling anytime before delivery
     * 
     * If the order does not exists it does not throw an exception 
     *
     * @param orderId The ID of the order to cancel.
     * @return PancakeOperationResult 
     */
    public PancakeOperationResult cancelOrder(UUID orderId) {
        if (orderId == null) {
            return new PancakeOperationResult(false, orderId, "Order ID cannot be null.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new PancakeOperationResult(false, orderId, "Order not found.");
        }
        
        try {
        	pancakeManager.cancel(order);
            order.updateRepository(orderRepository);
            return new PancakeOperationResult(true, order.getId());
        } catch (Exception e) {
        	return new PancakeOperationResult(false, orderId, e.getMessage()); 
        }
        
    }

    /**
     * Marks an order as completed.
     *
     * @param orderId The ID of the order to complete.
     * @return PancakeOperationResult 
     */
    public PancakeOperationResult completeOrder(UUID orderId) {
        if (orderId == null) {
            return new PancakeOperationResult(false, orderId, "Order ID cannot be null.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new PancakeOperationResult(false, orderId, "Order not found.");
        }
        
        try {
        	pancakeManager.complete(order);
        	order.updateRepository(orderRepository);
        	return new PancakeOperationResult(true, order.getId());
        } catch (Exception e) {
        	return new PancakeOperationResult(false, orderId, e.getMessage());  
        }

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
     * @return PancakeOperationResult
     */
    public PancakeOperationResult prepareOrder(UUID orderId) {
        if (orderId == null) {
            return new PancakeOperationResult(false, orderId, "Order ID cannot be null.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new PancakeOperationResult(false, orderId, "Order not found.");
        }
        
        try {
        	pancakeManager.prepare(order);
        	order.updateRepository(orderRepository);
        	return new PancakeOperationResult(true, order.getId());
        } catch (Exception e) {
        	return new PancakeOperationResult(false, orderId, e.getMessage());
        }
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
        if (orderId == null) {
            return new DeliveryResult(false, orderId, "Order ID cannot be null.");
        }

        OrderInterface order = getOrder(orderId);
        if (!order.isValid()) {
            return new DeliveryResult(false, orderId, "Order not found.");
        }
        
        try {
	    	pancakeManager.deliver(order);
	    	order.updateRepository(orderRepository);
	
	        return new DeliveryResult(order.isDelivered(), order.getId(),  order.getPancakesToDeliver(), "");
        } catch (Exception e) {
        	return new DeliveryResult(false, order.getId(), new LinkedList<String>(), e.getMessage());
        }
    }
}
