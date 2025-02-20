package org.pancakelab.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void GivenValidBuildingAndRoom_WhenCreatingOrder_ThenOrderIsCreatedSuccessfully() {
        Order order = new Order(10, 20);
        
        assertEquals(order.getBuilding(), 10);
        assertEquals(order.getRoom(), 20);
        assertNotNull(order.getId());
    }

    @Test
    public void GivenInvalidBuildingOrRoom_WhenCreatingOrder_ThenThrowException() {
        Exception ex1  = Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new Order(-1, 10));
        assertEquals(ex1.getMessage(), "Building number must be greater than zero.");
        
        Exception ex2  = Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new Order(10, -1));
        assertEquals(ex2.getMessage(), "Room number must be greater than zero.");
    }

    @Test
    public void GivenTwoOrdersWithDifferentIds_WhenCheckingEquality_ThenTheyAreNotEqual() {
        Order order1 = new Order(10, 20);
        Order order2 = new Order(10, 20); 
        
        
        // Different UUID 
        assertNotEquals(order1, order2);
        assertNotEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    public void GivenSameOrderReference_WhenCheckingEquality_ThenTheyAreEqual() {
        Order order = new Order(10, 20);
        
        assertEquals(order, order);
        assertEquals(order.hashCode(), order.hashCode());
    }

    @Test
    public void GivenNullOrDifferentClass_WhenCheckingEquality_ThenTheyAreNotEqual() {
        // Arrange
        Order order = new Order(10, 20);

        // Act & Assert
        assertNotEquals(order, null);
        assertNotEquals(order, "Some String");
    }
    
    @Test
    void testSetStatus_ValidTransitions() {
    	Order order = new Order(1,2);
    	
    	assertTrue(order.isInitated());
    	
        // Test INITIATED -> COMPLETED
        order.completed();
        assertTrue(order.isCompleted());

        // Test COMPLETED -> PREPARED
        order.prepared();
        assertTrue(order.isPrepared());

        // Test PREPARED -> DELIVERED
        order.completed();
        order.delivered();
        assertTrue(order.isDelivered());
    }

    @Test
    void testSetStatus_InvalidTransitions() {
    	Order order = new Order(1,2);
    	
        // Test INITIATED -> DELIVERED (Invalid)
        order.delivered();
        assertFalse(order.isDelivered());

        // Test INITIATED -> PREPARED (Invalid)
        order.prepared();
        assertFalse(order.isPrepared());
        
        // Test COMPLETED -> DELIVERED (Invalid)
        order.completed();
        order.delivered();
        assertFalse(order.isDelivered());
    }
    
    @Test
    void testIsInitiated() {
    	Order order = new Order(1,2);

        assertTrue(order.isInitated(), "Order should be in INITIATED status");
        assertFalse(order.isCompleted());
        assertFalse(order.isPrepared());
        assertFalse(order.isDelivered());
        
        
    	order.completed();
    	
        assertTrue(order.isCompleted(), "Order should be in COMPLETED status");
        assertFalse(order.isInitated());
        assertFalse(order.isPrepared());
        assertFalse(order.isDelivered());
        
        order.prepared();
    	
        assertTrue(order.isPrepared(), "Order should be in PREPARED status");
        assertFalse(order.isInitated());
        assertFalse(order.isCompleted());
        assertFalse(order.isDelivered());
        
        order.delivered();

        assertTrue(order.isDelivered(), "Order should be in DELIVERED status");
        assertFalse(order.isInitated());
        assertFalse(order.isCompleted());
        assertFalse(order.isPrepared());
    }
    
}
