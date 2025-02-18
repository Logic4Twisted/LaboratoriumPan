package org.pancakelab.model;

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
    public String toString() {
        return "NullOrder{}";
    }
}

