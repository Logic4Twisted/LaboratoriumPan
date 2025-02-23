package org.pancakelab.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

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
    
    private final ReentrantLock lock = new ReentrantLock();
    
    public Order(int building, int room) {
    	validateBuildingAndRoom(building, room);
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
        status = OrderStatus.INITIATED;
        pancakes = new LinkedList<PancakeRecipe>();
    }
    
    private void validateBuildingAndRoom(int building, int room) {
        if (building <= 0) {
            throw new IllegalArgumentException("Building number must be greater than zero.");
        }
        if (room <= 0) {
            throw new IllegalArgumentException("Room number must be greater than zero.");
        }
    }
    
    private void lock() {
    	lock.lock();
    }
    
    private void unlock() {
    	lock.unlock();
    }
    
    private void changeStatus(OrderStatus nextStatus) {
    	lock();
    	try {
    		if (status == OrderStatus.INITIATED && nextStatus == OrderStatus.COMPLETED) {
        		this.status = nextStatus;
        	} else if (status == OrderStatus.COMPLETED && nextStatus == OrderStatus.PREPARED) {
        		this.status = nextStatus;
        	} else if (status == OrderStatus.PREPARED && nextStatus == OrderStatus.DELIVERED) {
        		this.status = nextStatus;
        	}
    	} finally {
    		unlock();
    	}
    	
    }
    
    public void addPancake(List<String> ingredients) {
    	lock();
    	try {
	    	if (!isInitated()) {
	    		return;
	    	}
	    	Pancake pancake = new Pancake(ingredients);
	    	pancakes.add(pancake);
	    	OrderLog.logAddPancake(this, pancake.description(), getPancakes().size());
    	} finally {
    		unlock();
    	}
    }
    
    public boolean removePancake(String description) {
    	lock();
    	try {
	    	if (!isInitated()) {
	    		return false;
	    	}
	    	Optional<PancakeRecipe> pancake = pancakes.stream()
	    		.filter(p -> p.description().equals(description))
	    		.findFirst();
	    	if (pancake.isPresent()) {
	    		pancakes.remove(pancake.get());
	    		OrderLog.logRemovePancakes(this, description, 1, getPancakes().size());
	    		return true;
	    	}
	    	return false;
    	} finally {
    		unlock();
    	}
    }
    
    public List<String> getPancakes() {
    	lock();
    	try {
    		return pancakes.stream().map(PancakeRecipe::description).toList();
    	} finally {
    		unlock();
    	}
    }
    
    public List<String> getPancakesToDeliver() {
    	lock();
    	try {
        	if (isDelivered()) {
        		return getPancakes();
        	}
        	return new LinkedList<String>();
    	} finally {
    		unlock();
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
    
    public void completed(OrderRepository orderRepository) {
    	changeStatus(OrderStatus.COMPLETED);
    	orderRepository.save(this);
    }
    
    public void prepared(OrderRepository orderRepository) {
    	changeStatus(OrderStatus.PREPARED);
    	orderRepository.save(this);
    }
    
    public void delivered(OrderRepository orderRepository) {
    	changeStatus(OrderStatus.DELIVERED);
    	if (isDelivered()) {
    		orderRepository.delete(getId());
    		OrderLog.logDeliverOrder(this, getPancakes().size());
    	}
    }
    
    public void cancel(OrderRepository orderRepository) {
    	orderRepository.delete(getId());
    	OrderLog.logCancelOrder(this, getPancakes().size());
    }
    
    public void saveTo(OrderRepository orderRepository) {
    	orderRepository.save(this);
    }
    
    public boolean isInitated() {
    	return getStatus() == OrderStatus.INITIATED;
    }
    
    public boolean isCompleted() {
    	return getStatus() == OrderStatus.COMPLETED;
    }
    
    public boolean isPrepared() {
    	return getStatus() == OrderStatus.PREPARED;
    }
    
    public boolean isDelivered() {
    	return getStatus() == OrderStatus.DELIVERED;
    }
    
    private OrderStatus getStatus() {
    	return status;
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
