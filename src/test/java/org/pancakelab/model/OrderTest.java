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
}
