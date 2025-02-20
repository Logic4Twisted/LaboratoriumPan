package org.pancakelab.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.pancakelab.model.pancakes.PancakeRecipe;

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
    
    private void setStatus(OrderStatus nextStatus) {
    	if (status == OrderStatus.INITIATED && nextStatus == OrderStatus.COMPLETED) {
    		this.status = nextStatus;
    	} else if (status == OrderStatus.COMPLETED && nextStatus == OrderStatus.PREPARED) {
    		this.status = nextStatus;
    	} else if (status == OrderStatus.PREPARED && nextStatus == OrderStatus.DELIVERED) {
    		this.status = nextStatus;
    	}
    }
    
    public List<PancakeRecipe> getPancakes() {
    	return new LinkedList<PancakeRecipe>(pancakes);
    }
    
    public void addPancake(PancakeRecipe pancake) {
    	pancakes.add(pancake);
    }
    
    public void removePancake(PancakeRecipe pancake) {
    	pancakes.stream()
    		.filter(p -> p.equals(pancake))
    		.findFirst()
    		.ifPresent(pancakes::remove);
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
    	setStatus(OrderStatus.COMPLETED);
    }
    
    public void prepared() {
    	setStatus(OrderStatus.PREPARED);
    }
    
    public void delivered() {
    	setStatus(OrderStatus.DELIVERED);
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
