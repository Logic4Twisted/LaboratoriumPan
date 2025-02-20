package org.pancakelab.model;

import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final int building;
    private final int room;
    private OrderStatus status;

    public Order(int building, int room) {
    	validateBuildingAndRoom(building, room);
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
        status = OrderStatus.INITIATED;
    }
    
    private void validateBuildingAndRoom(int building, int room) {
        if (building <= 0) {
            throw new IllegalArgumentException("Building number must be greater than zero.");
        }
        if (room <= 0) {
            throw new IllegalArgumentException("Room number must be greater than zero.");
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
    
    private void setStatus(OrderStatus nextStatus) {
    	if (status == OrderStatus.INITIATED && nextStatus == OrderStatus.COMPLETED) {
    		this.status = nextStatus;
    	} else if (status == OrderStatus.COMPLETED && nextStatus == OrderStatus.PREPARED) {
    		this.status = nextStatus;
    	} else if (status == OrderStatus.PREPARED && nextStatus == OrderStatus.DELIVERED) {
    		this.status = nextStatus;
    	}
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
