package org.pancakelab.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.pancakelab.model.pancakes.PancakeRecipe;

public class NullOrder extends Order {
    private static NullOrder instance;

    private NullOrder() {
        super(1, 1);
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
    
    public void completed() {
    	// do nothing
    }
    
    public void prepared() {
    	// do nothing
    }
    
    public void delivered() {
    	// do nothing
    }
    
    public List<PancakeRecipe> getPancakes() {
    	return new LinkedList<PancakeRecipe>();
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

}

