package org.pancakelab.model.pancakes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

class PancakeTest {

    private Pancake pancake1;
    private Pancake pancake2;
    private UUID orderId1;
    private UUID orderId2;

    @BeforeEach
    void setUp() {
        orderId1 = UUID.randomUUID();
        orderId2 = UUID.randomUUID();
        pancake1 = new Pancake(orderId1);
        pancake2 = new Pancake(orderId2);
    }

    @Test
    void testConstructor_WithOrderId() {
        assertEquals(orderId1, pancake1.getOrderId(), "Order ID should be set by constructor");
    }

    @Test
    void testConstructor_Default() {
        Pancake pancake = new Pancake();
        assertNull(pancake.getOrderId(), "Default constructor should set orderId to null");
        assertTrue(pancake.ingredients().isEmpty(), "Default constructor should initialize an empty ingredients list");
    }

    @Test
    void testSetOrderId() {
        UUID newOrderId = UUID.randomUUID();
        pancake1.setOrderId(newOrderId);
        assertEquals(newOrderId, pancake1.getOrderId(), "Order ID should be updated");
    }

    @Test
    void testAddIngredient() {
        pancake1.addIngredient("Flour");
        pancake1.addIngredient("Milk");

        List<String> expectedIngredients = List.of("Flour", "Milk");
        assertEquals(expectedIngredients, pancake1.ingredients(), "Ingredients list should match added ingredients");
    }

    @Test
    void testEquals_SameIngredients_DifferentOrder() {
        pancake1.addIngredient("Flour");
        pancake1.addIngredient("Milk");

        pancake2.addIngredient("Milk");
        pancake2.addIngredient("Flour");

        assertTrue(pancake1.equals(pancake2), "Pancakes with the same ingredients in different order should be equal");
    }

    @Test
    void testEquals_DifferentIngredients() {
        pancake1.addIngredient("Flour");
        pancake2.addIngredient("Eggs");

        assertFalse(pancake1.equals(pancake2), "Pancakes with different ingredients should not be equal");
    }

    @Test
    void testEquals_DifferentObjectTypes() {
        assertFalse(pancake1.equals(new Object()), "Pancake should not be equal to an unrelated object");
    }

    @Test
    void testEquals_SameReference() {
        assertTrue(pancake1.equals(pancake1), "Pancake should be equal to itself");
    }

    @Test
    void testEquals_Null() {
        assertFalse(pancake1.equals(null), "Pancake should not be equal to null");
    }
}
