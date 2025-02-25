package org.pancakelab.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;
import org.pancakelab.model.pancakes.OrderRepository;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.PancakeRecipe;

import java.util.List;

class OrderManagerTest {

    private Order order;
    private List<String> darkChocolateRecepie = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE);
    private List<String> milkChocolateRecepie = List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE);
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        order = new Order(1,2);
        orderRepository = new InMemoryOrderRepository();
    }

    @Test
    void testGetPancakeRecipes_EmptyList() {
        List<String> recipes = order.getPancakes();
        assertTrue(recipes.isEmpty(), "Pancake list should initially be empty");
    }

    @Test
    void testAddPancake() {
        order.addPancake(darkChocolateRecepie);
        List<String> recipes = order.getPancakes();

        assertEquals(1, recipes.size(), "List should contain one pancake recipe");
        assertTrue(recipes.contains(pancakeDescrption(darkChocolateRecepie)), "List should contain the added pancake");
    }

    @Test
    void testRemovePancake_Existing() {
        order.addPancake(darkChocolateRecepie);
        order.addPancake(milkChocolateRecepie);

        order.removePancake(pancakeDescrption(darkChocolateRecepie));
        List<String> recipes = order.getPancakes();

        assertEquals(1, recipes.size(), "List should contain only one pancake after removal");
        assertFalse(recipes.contains(pancakeDescrption(darkChocolateRecepie)), "Removed pancake should not be in the list");
        assertTrue(recipes.contains(pancakeDescrption(milkChocolateRecepie)), "Unremoved one should still be there");
    }

    @Test
    void testRemovePancake_NonExisting() {
        order.addPancake(darkChocolateRecepie);

        PancakeRecipe nonExisting = new Pancake(List.of(ApprovedIngredients.INGREDIENT_HAZELNUTS, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM));
        order.removePancake(nonExisting.description());

        List<String> recipes = order.getPancakes();
        assertEquals(1, recipes.size(), "List should still contain one pancake");
        assertTrue(recipes.contains(pancakeDescrption(darkChocolateRecepie)), "Original pancake should still be in the list");
    }

    @Test
    void testGetPancakeRecipes_ModificationDoesNotAffectOriginalList() {
        order.addPancake(darkChocolateRecepie);
        List<String> recipes = order.getPancakes();
     
        
        // Verify that adding an element throws UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            recipes.add(pancakeDescrption(milkChocolateRecepie));
        }); // Try modifying the returned list
    }
    
    @Test
    void testCannotAddPancakesAfterCompletion() {
        Order order = new Order(1, 2);

        // Add pancakes while order is INITIATED
        order.addPancake(darkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "Pancake should be added in INITIATED state");

        // Complete the order
        order.complete();
        assertTrue(order.isCompleted(), "Order should be in COMPLETED state");

        // Try adding another pancake (should not be added)
        order.addPancake(milkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "No additional pancakes should be added after COMPLETED state");
    }
    
    @Test
    void testCannotRemovePancakesAfterCompletion() {
        Order order = new Order(1, 2);

        // Add pancake while order is INITIATED
        order.addPancake(darkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "Pancake should be added in INITIATED state");

        // Complete the order
        order.complete();
        assertTrue(order.isCompleted(), "Order should be in COMPLETED state");

        // Attempt to remove pancake (should not be removed)
        boolean removed = order.removePancake(pancakeDescrption(darkChocolateRecepie));
        assertFalse(removed, "Pancake should not be removed after order completion");
        assertEquals(1, order.getPancakes().size(), "Pancake list should remain unchanged after order completion");
    }
    
    @Test
    void testCannotModifyPancakesAfterPreparationOrDelivery() {
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
        boolean removed = order.removePancake(pancakeDescrption(darkChocolateRecepie));
        assertFalse(removed, "Pancake should not be removed in PREPARED state");
        assertEquals(1, order.getPancakes().size(), "Pancake list should remain unchanged in PREPARED state");

        // Move to DELIVERED state
        order.deliver();
        assertTrue(order.isDelivered(), "Order should be in DELIVERED state");

        // Try adding a pancake (should not be added)
        order.addPancake(milkChocolateRecepie);
        assertEquals(1, order.getPancakes().size(), "No additional pancakes should be added in DELIVERED state");

        // Try removing a pancake (should not be removed)
        removed = order.removePancake("Banana Pancake");
        assertFalse(removed, "Pancake should not be removed in DELIVERED state");
        assertEquals(1, order.getPancakes().size(), "Pancake list should remain unchanged in DELIVERED state");
    }

    private String pancakeDescrption(List<String> ingredients) {
    	return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
    }
    
    
}

