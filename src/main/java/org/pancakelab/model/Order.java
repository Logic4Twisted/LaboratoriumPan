package org.pancakelab.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.service.OrderLog;

public class Order {
    private final UUID id;
    private final int building;
    private final int room;
    private OrderStatus status;
    
    private List<PancakeRecipe> pancakes;

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
    
    private void changeStatus(OrderStatus nextStatus) {
    	if (status == OrderStatus.INITIATED && nextStatus == OrderStatus.COMPLETED) {
    		this.status = nextStatus;
    	} else if (status == OrderStatus.COMPLETED && nextStatus == OrderStatus.PREPARED) {
    		this.status = nextStatus;
    	} else if (status == OrderStatus.PREPARED && nextStatus == OrderStatus.DELIVERED) {
    		this.status = nextStatus;
    	}
    }
    
    public void addPancake(List<String> ingredients) {
    	if (!isInitated()) {
    		return;
    	}
    	Pancake pancake = new Pancake(ingredients);
    	pancakes.add(pancake);
    	OrderLog.logAddPancake(this, pancake.description(), this.getPancakes().size());
    }
    
    public boolean removePancake(String description) {
    	if (!isInitated()) {
    		return false;
    	}
    	Optional<PancakeRecipe> pancake = pancakes.stream()
    		.filter(p -> p.description().equals(description))
    		.findFirst();
    	if (pancake.isPresent()) {
    		pancakes.remove(pancake.get());
    		return true;
    	}
    	return false;
    }
    
    public List<String> getPancakes() {
    	return pancakes.stream().map(PancakeRecipe::description).toList();
    }
    
    public List<String> getPancakesToDeliver() {
    	if (isDelivered()) {
    		return getPancakes();
    	}
    	return new LinkedList<String>();
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
    
    public void completed() {
    	changeStatus(OrderStatus.COMPLETED);
    }
    
    public void prepared() {
    	changeStatus(OrderStatus.PREPARED);
    }
    
    public void delivered() {
    	changeStatus(OrderStatus.DELIVERED);
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
