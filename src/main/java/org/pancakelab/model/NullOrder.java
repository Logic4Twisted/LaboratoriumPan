package org.pancakelab.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.pancakelab.model.pancakes.OrderRepository;
import org.pancakelab.model.pancakes.PancakeRecipe;

public class NullOrder implements OrderInterface {
    private static NullOrder instance;

    private NullOrder() {
    }

    public static NullOrder getInstance() {
    	if (instance == null) {
    		instance = new NullOrder();
    	}
        return instance;
    }

    @Override
    public UUID getId() {
        return new UUID(0, 0); // Return a fixed, invalid UUID
    }

    @Override
    public int getBuilding() {
        return 0; // Indicate an invalid building
    }

    @Override
    public int getRoom() {
        return 0; // Indicate an invalid room
    }
    
    @Override
    public boolean isInitated() {
    	return false;
    }
    
    @Override
    public boolean isCompleted() {
    	return false;
    }
    
    @Override
    public boolean isPrepared() {
    	return false;
    }
    
    @Override
    public boolean isDelivered() {
    	return false;
    }
    
    @Override
    public void completed() {
    	// do nothing
    }
    
    public void prepared() {
    	// do nothing
    }
    
    public void delivered() {
    	// do nothing
    }
    
    public List<String> getPancakes() {
    	return new LinkedList<String>();
    }
    
    public void addPancake(PancakeRecipe pancake) {
    	// do nothing
    }
    
    public boolean removePancake(String description) {
    	return false;
    }

    
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 0;
    }

	@Override
	public void addPancake(List<String> ingredients) {
	}

	@Override
	public List<String> getPancakesToDeliver() {
		return new LinkedList<String>();
	}

	@Override
	public void cancel() {
	}

	@Override
	public void saveTo(OrderRepository orderRepository) {
	}

	@Override
	public void delete(OrderRepository orderRepository) {
	}

}

