package org.pancakelab.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.pancakelab.model.pancakes.OrderRepository;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.service.OrderLog;

public class Order implements OrderInterface {
    private final UUID id;
    private final int building;
    private final int room;
    private OrderStatus status;
    
    private List<PancakeRecipe> pancakes;
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    public Order(int building, int room) {
    	validateBuildingAndRoom(building, room);
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
        status = OrderStatus.INITIATED;
        pancakes = new LinkedList<PancakeRecipe>();
    }
    
    private static final Map<OrderStatus, OrderStatus> STATUS_TRANSITIONS = Map.of(
            OrderStatus.INITIATED, OrderStatus.COMPLETED,
            OrderStatus.COMPLETED, OrderStatus.PREPARED,
            OrderStatus.PREPARED, OrderStatus.DELIVERED
    );
    
    private void validateBuildingAndRoom(int building, int room) {
        if (building <= 0) {
            throw new IllegalArgumentException("Building number must be greater than zero.");
        }
        if (room <= 0) {
            throw new IllegalArgumentException("Room number must be greater than zero.");
        }
    }
    
    private void changeStatus(OrderStatus nextStatus) {
    	lock.writeLock().lock();
    	try {
    		if (STATUS_TRANSITIONS.getOrDefault(status, null) == nextStatus) {
    			status = nextStatus;
    		}
    	} finally {
    		lock.writeLock().unlock();
    	}
    }
    
    public void addPancake(List<String> ingredients) {
    	if (!isInitated()) return;
    	lock.writeLock().lock();
    	try {
	    	Pancake pancake = new Pancake(ingredients);
	    	pancakes.add(pancake);
	    	OrderLog.logAddPancake(this, pancake.description(), getPancakes().size());
    	} finally {
    		lock.writeLock().unlock();
    	}
    }
    
    public boolean removePancake(String description) {
    	if (!isInitated()) return false;
    	
    	lock.writeLock().lock();
    	try {
    		for (Iterator<PancakeRecipe> iterator = pancakes.iterator(); iterator.hasNext(); ) {
	            PancakeRecipe pancake = iterator.next();
	            if (pancake.description().equals(description)) {
	                iterator.remove();
	                OrderLog.logRemovePancakes(this, description, 1, getPancakes().size());
	                return true;
	            }
	        }
	        return false; // ❌ No pancake found with this description
    	} finally {
    		lock.writeLock().unlock();
    	}
    }
    
    public List<String> getPancakes() {
    	lock.readLock().lock();
    	try {
    		return pancakes.stream().map(PancakeRecipe::description).toList();
    	} finally {
    		lock.readLock().unlock();
    	}
    }
    
    public List<String> getPancakesToDeliver() {
    	lock.readLock().lock();
    	try {
        	if (isDelivered()) {
        		return getPancakes();
        	}
        	return new LinkedList<String>();
    	} finally {
    		lock.readLock().unlock();
    	}
    }

    public UUID getId() {
        return id;
    }

    public int getBuilding() {
        return building;
    }

    public int getRoom() {
        return room;
    }
    
    public void complete() {
    	changeStatus(OrderStatus.COMPLETED);
    }
    
    public void prepare() {
    	changeStatus(OrderStatus.PREPARED);
    }
    
    public void deliver() {
    	changeStatus(OrderStatus.DELIVERED);
    	if (isDelivered()) {
    		OrderLog.logDeliverOrder(this, getPancakes().size());
    	}
    }
    
    public void cancel() {
    	OrderLog.logCancelOrder(this, getPancakes().size());
    }
    
    public void saveTo(OrderRepository orderRepository) {
    	lock.readLock().lock();
    	try {
    		orderRepository.save(this);
    	} finally {
    		lock.readLock().unlock();
    	}
    }
    
    public void delete(OrderRepository orderRepository) {
    	lock.readLock().lock();
    	try {
    		orderRepository.delete(id);
    	} finally {
    		lock.readLock().unlock();
    	}
    }
    
    public boolean isInitated() {
    	return checkIfStatus(OrderStatus.INITIATED);
    }
    
    public boolean isCompleted() {
    	return checkIfStatus(OrderStatus.COMPLETED);
    }
    
    public boolean isPrepared() {
    	return checkIfStatus(OrderStatus.PREPARED);
    }
    
    public boolean isDelivered() {
    	return checkIfStatus(OrderStatus.DELIVERED);
    }
    
    private boolean checkIfStatus(OrderStatus qStatus) {
    	lock.readLock().lock();
    	try {
    		return status == qStatus;
    	} finally {
			lock.readLock().unlock();
		}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
