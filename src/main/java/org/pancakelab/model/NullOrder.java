package org.pancakelab.model;

import java.util.Objects;
import java.util.UUID;

public class NullOrder extends Order {
    private static final NullOrder INSTANCE = new NullOrder();

    private NullOrder() {
        super(0, 0);
    }

    public static NullOrder getInstance() {
        return INSTANCE;
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
    
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}

