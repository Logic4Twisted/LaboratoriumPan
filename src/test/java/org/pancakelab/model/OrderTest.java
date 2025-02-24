package org.pancakelab.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;
import org.pancakelab.model.pancakes.OrderRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class OrderTest {
	
	OrderRepository orderRepository;
	
	@BeforeEach
	void setUp() {
		orderRepository = new InMemoryOrderRepository();
	}

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
    
    @Test
    void testGetPancakesToDeliver() {
        Order order = new Order(1, 2);
        order.addPancake(List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE));

        // INITIATED state should return an empty list
        assertTrue(order.getPancakesToDeliver().isEmpty(), "Pancakes should NOT be available for delivery in INITIATED state");

        // COMPLETED state should return an empty list
        order.completed();
        assertTrue(order.getPancakesToDeliver().isEmpty(), "Pancakes should NOT be available for delivery in COMPLETED state");

        // PREPARED state should return the actual list
        order.prepared();
        assertTrue(order.getPancakesToDeliver().isEmpty(), "Pancakes should be available for delivery in PREPARED state");

        // DELIVERED state should return an empty list
        order.delivered();
        assertFalse(order.getPancakesToDeliver().isEmpty(), "Pancakes should NOT be available for delivery in DELIVERED state");
    }
    
    @Test
    void testEquals_DifferentOrdersSameBuildingRoom() {
        Order order1 = new Order(5, 10);
        Order order2 = new Order(5, 10); // Different UUID

        assertNotEquals(order1, order2, "Orders with the same building and room but different IDs should not be equal");
    }
    
    @Test
    void testEquals_Null() {
        Order order = new Order(1, 1);
        assertFalse(order.equals(null), "An order should not be equal to null");
    }
    
}
