package org.pancakelab.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.ApprovedIngredients;
import org.pancakelab.model.Order;

import java.util.*;

class PancakeManagerTest {

    private PancakeManagerImpl pancakeManager;
    private Order order;
    private static final List<String> SAMPLE_INGREDIENTS = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM);

    @BeforeEach
    void setUp() {
        pancakeManager = new PancakeManagerImpl();
        order = new Order(1, 101); // Building 1, Room 101
    }

    // Test Adding Pancakes Successfully
    @Test
    void testAddPancakes_Success() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 5);
        assertEquals(5, order.getPancakes().size(), "Should add 5 pancakes to the order");
    }

    // Test Adding More Than MAX_PANCAKE_COUNT (Limit at 100)
    @Test
    void testAddPancakes_RespectsMaxPancakeCount() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 150);
        assertEquals(100, order.getPancakes().size(), "Should not exceed MAX_PANCAKE_COUNT (100)");
    }

    // Test Adding More Than MAX_PANCAKE_PER_ORDER (Limit at 500)
    @Test
    void testAddPancakes_RespectsMaxPancakePerOrder() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 500);
        assertEquals(PancakeManagerImpl.MAX_PANCAKE_COUNT, order.getPancakes().size(), "Should not exceed MAX_PANCAKE_PER_ORDER (500)");

        // Try adding more after reaching 500
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 10);
        assertEquals(PancakeManagerImpl.MAX_PANCAKE_COUNT + 10, order.getPancakes().size(), "Should not allow adding pancakes beyond MAX_PANCAKE_PER_ORDER");
    }

    // Test Adding Zero Pancakes
    @Test
    void testAddPancakes_ZeroCount() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 0);
        assertEquals(0, order.getPancakes().size(), "Adding zero pancakes should not modify the order");
    }

    // Test Adding Negative Count (Should Not Add)
    @Test
    void testAddPancakes_NegativeCount() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, -5);
        assertEquals(0, order.getPancakes().size(), "Negative count should not add pancakes");
    }

    // Test Adding Pancakes to Null Order
    @Test
    void testAddPancakes_NullOrder() {
        assertThrows(NullPointerException.class, () -> {
            pancakeManager.addPancakes(null, SAMPLE_INGREDIENTS, 5);
        }, "Adding pancakes to a null order should throw NullPointerException");
    }

    // Test Removing Pancakes Successfully
    @Test
    void testRemovePancakes_Success() {
        // Add 5 pancakes first
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 5);

        // Remove 3 pancakes
        pancakeManager.removePancakes(order, description(SAMPLE_INGREDIENTS), 3);

        assertEquals(2, order.getPancakes().size(), "Should remove 3 pancakes");
    }

    // Test Removing More Than Available (Should Not Go Negative)
    @Test
    void testRemovePancakes_ExceedingAvailable() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 3);
        pancakeManager.removePancakes(order, description(SAMPLE_INGREDIENTS), 10);

        assertEquals(0, order.getPancakes().size(), "Removing more than available should result in an empty order");
    }

    // Test Removing Zero Pancakes (No Effect)
    @Test
    void testRemovePancakes_ZeroCount() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 5);
        pancakeManager.removePancakes(order, description(SAMPLE_INGREDIENTS), 0);

        assertEquals(5, order.getPancakes().size(), "Removing zero pancakes should not modify the order");
    }

    // Test Removing Negative Count (Should Not Remove)
    @Test
    void testRemovePancakes_NegativeCount() {
        pancakeManager.addPancakes(order, SAMPLE_INGREDIENTS, 5);
        pancakeManager.removePancakes(order, description(SAMPLE_INGREDIENTS), -3);

        assertEquals(5, order.getPancakes().size(), "Negative removal count should not remove pancakes");
    }

    // Test Removing from Empty Order (No Effect)
    @Test
    void testRemovePancakes_EmptyOrder() {
        pancakeManager.removePancakes(order, description(SAMPLE_INGREDIENTS), 3);
        assertEquals(0, order.getPancakes().size(), "Removing from an empty order should have no effect");
    }

    //Test Removing Pancakes from Null Order
    @Test
    void testRemovePancakes_NullOrder() {
        assertThrows(NullPointerException.class, () -> {
            pancakeManager.removePancakes(null, description(SAMPLE_INGREDIENTS), 3);
        }, "Removing pancakes from a null order should throw NullPointerException");
    }
    
    String description(List<String> ingredients) {
    	 return "Delicious pancake with %s!".formatted(String.join(", ", ingredients)); 
    }
}
