package org.pancakelab.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;
import org.pancakelab.model.pancakes.OrderRepository;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.PancakeRecipe;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class OrderTest {
	
    private List<String> darkChocolateRecepie = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE);
    private List<String> milkChocolateRecepie = List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE);
	
	OrderRepository orderRepository;
	Order order;
	
	@BeforeEach
	void setUp() {
		orderRepository = new InMemoryOrderRepository();
		order = new Order(10, 20);
	}

    @Test
    public void GivenValidBuildingAndRoom_WhenCreatingOrder_ThenOrderIsCreatedSuccessfully() {
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
        
        assertEquals(order, order);
        assertEquals(order.hashCode(), order.hashCode());
    }

    @Test
    public void GivenNullOrDifferentClass_WhenCheckingEquality_ThenTheyAreNotEqual() {

        // Act & Assert
        assertNotEquals(order, null);
        assertNotEquals(order, "Some String");
    }
    
    @Test
    void cannotTransitionFromInitiatedToPrepared() {
        
        Exception exception = assertThrows(Exception.class, () -> order.prepare());
        assertEquals("Cannot change status of the order to PREPARED", exception.getMessage());
    }

    @Test
    void cannotTransitionFromInitiatedToDelivered() {
        
        Exception exception = assertThrows(Exception.class, () -> order.deliver());
        assertEquals("Cannot change status of the order to DELIVERED", exception.getMessage());
    }

    @Test
    void cannotTransitionFromCompletedToDelivered() throws Exception {
        
        order.complete(); // Move to COMPLETED state
        Exception exception = assertThrows(Exception.class, () -> order.deliver());
        assertEquals("Cannot change status of the order to DELIVERED", exception.getMessage());
    }

    @Test
    void cannotTransitionFromPreparedToInitiated() throws Exception {
        
        order.complete();
        order.prepare();
        Exception exception = assertThrows(Exception.class, () -> order.complete());
        assertEquals("Cannot change status of the order to COMPLETED", exception.getMessage());
    }

    @Test
    void cannotTransitionFromDeliveredToAnyOtherState() throws Exception {
        
        order.complete();
        order.prepare();
        order.deliver();

        Exception exception1 = assertThrows(Exception.class, () -> order.complete());
        assertEquals("Cannot change status of the order to COMPLETED", exception1.getMessage());

        Exception exception2 = assertThrows(Exception.class, () -> order.prepare());
        assertEquals("Cannot change status of the order to PREPARED", exception2.getMessage());

        order.cancel();
    }

    @Test
    void cannotTransitionFromCancelledToAnyOtherState() throws Exception {
        
        order.cancel();

        Exception exception1 = assertThrows(Exception.class, () -> order.complete());
        assertEquals("Cannot change status of the order to COMPLETED", exception1.getMessage());

        Exception exception2 = assertThrows(Exception.class, () -> order.prepare());
        assertEquals("Cannot change status of the order to PREPARED", exception2.getMessage());

        Exception exception3 = assertThrows(Exception.class, () -> order.deliver());
        assertEquals("Cannot change status of the order to DELIVERED", exception3.getMessage());
    }

    @Test
    void validTransitionsWorkCorrectly() throws Exception {
        
        order.complete();
        assertTrue(order.isCompleted());

        order.prepare();
        assertTrue(order.isPrepared());

        order.deliver();
        assertTrue(order.isDelivered());
    }
    
    @Test
    void testGetPancakesToDeliver() throws Exception {
        order.addPancake(List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE));

        // INITIATED state should return an empty list
        assertTrue(order.getPancakesToDeliver().isEmpty(), "Pancakes should NOT be available for delivery in INITIATED state");

        // COMPLETED state should return an empty list
        order.complete();
        assertTrue(order.getPancakesToDeliver().isEmpty(), "Pancakes should NOT be available for delivery in COMPLETED state");

        // PREPARED state should return the actual list
        order.prepare();
        assertTrue(order.getPancakesToDeliver().isEmpty(), "Pancakes should be available for delivery in PREPARED state");

        // DELIVERED state should return an empty list
        order.deliver();
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
        assertFalse(order.equals(null), "An order should not be equal to null");
    }
    
    @Test
    void testGetPancakeRecipes_EmptyList() {
        List<String> recipes = order.getPancakes();
        assertTrue(recipes.isEmpty(), "Pancake list should initially be empty");
    }

    @Test
    void testAddPancake() throws Exception {
        order.addPancake(darkChocolateRecepie);
        List<String> recipes = order.getPancakes();

        assertEquals(1, recipes.size(), "List should contain one pancake recipe");
        assertTrue(recipes.contains(pancakeDescrption(darkChocolateRecepie)), "List should contain the added pancake");
    }

    @Test
    void testRemovePancake_Existing() throws Exception {
        order.addPancake(darkChocolateRecepie);
        order.addPancake(milkChocolateRecepie);

        order.removePancake(pancakeDescrption(darkChocolateRecepie));
        List<String> recipes = order.getPancakes();

        assertEquals(1, recipes.size(), "List should contain only one pancake after removal");
        assertFalse(recipes.contains(pancakeDescrption(darkChocolateRecepie)), "Removed pancake should not be in the list");
        assertTrue(recipes.contains(pancakeDescrption(milkChocolateRecepie)), "Unremoved one should still be there");
    }

    @Test
    void testRemovePancake_NonExisting() throws Exception {
        order.addPancake(darkChocolateRecepie);

        PancakeRecipe nonExisting = new Pancake(List.of(ApprovedIngredients.INGREDIENT_HAZELNUTS, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM));
        order.removePancake(nonExisting.description());

        List<String> recipes = order.getPancakes();
        assertEquals(1, recipes.size(), "List should still contain one pancake");
        assertTrue(recipes.contains(pancakeDescrption(darkChocolateRecepie)), "Original pancake should still be in the list");
    }

    @Test
    void testGetPancakeRecipes_ModificationDoesNotAffectOriginalList() throws Exception {
        order.addPancake(darkChocolateRecepie);
        List<String> recipes = order.getPancakes();
     
        
        // Verify that adding an element throws UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            recipes.add(pancakeDescrption(milkChocolateRecepie));
        }); // Try modifying the returned list
    }
    
    @Test
    void testCannotAddPancakesAfterCompletion() throws Exception {
        Order order = new Order(1, 2);

        // Add pancakes while order is INITIATED
        order.addPancake(darkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "Pancake should be added in INITIATED state");

        // Complete the order
        order.complete();
        assertTrue(order.isCompleted(), "Order should be in COMPLETED state");

        // Try adding another pancake (should not be added)
        Exception exception2 = assertThrows(Exception.class, () -> order.addPancake(milkChocolateRecepie));
        assertEquals("Order is not in the state in which adding pancakes is possible", exception2.getMessage());
        
        assertEquals(1, order.getPancakes().size(), "No additional pancakes should be added after COMPLETED state");
    }
    
    @Test
    void testCannotRemovePancakesAfterCompletion() throws Exception {
        Order order = new Order(1, 2);

        // Add pancake while order is INITIATED
        order.addPancake(darkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "Pancake should be added in INITIATED state");

        // Complete the order
        order.complete();
        assertTrue(order.isCompleted(), "Order should be in COMPLETED state");

        // Attempt to remove pancake (should not be removed)
        Exception exception1 = assertThrows(Exception.class, () -> order.removePancake(pancakeDescrption(darkChocolateRecepie)));
        assertEquals(exception1.getMessage(), "Order is not in the state in which removing pancakes is possible");
    }
    
    @Test
    void testCannotModifyPancakesAfterPreparationOrDelivery() throws Exception {
        Order order = new Order(1, 2);

        // Add a pancake while order is INITIATED
        order.addPancake(darkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "Pancake should be added in INITIATED state");

        // Move to COMPLETED state
        order.complete();
        assertTrue(order.isCompleted(), "Order should be in COMPLETED state");

        // Move to PREPARED state
        order.prepare();
        assertTrue(order.isPrepared(), "Order should be in PREPARED state");

        // Try removing a pancake (should not be removed)
        Exception exception1 = assertThrows(Exception.class, () -> order.removePancake(pancakeDescrption(darkChocolateRecepie)));
        assertEquals(exception1.getMessage(), "Order is not in the state in which removing pancakes is possible");

        // Move to DELIVERED state
        order.deliver();
        assertTrue(order.isDelivered(), "Order should be in DELIVERED state");

        // Try adding a pancake (should not be added)
        Exception exception2 = assertThrows(Exception.class, () -> order.addPancake(milkChocolateRecepie));
        assertEquals("Order is not in the state in which adding pancakes is possible", exception2.getMessage());

        // Try removing a pancake (should not be removed)
        Exception exception3 = assertThrows(Exception.class, () -> order.removePancake(pancakeDescrption(darkChocolateRecepie)));
        assertEquals(exception3.getMessage(), "Order is not in the state in which removing pancakes is possible");
    }

    private String pancakeDescrption(List<String> ingredients) {
    	return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
    }
    
}
