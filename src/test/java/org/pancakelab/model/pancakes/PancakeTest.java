package org.pancakelab.model.pancakes;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.ApprovedIngredients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

class PancakeTest {

    private Pancake pancake1;
    private Pancake pancake2;
    private Pancake pancake3;

    @BeforeEach
    void setUp() {
        pancake1 = new Pancake(List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM));
        pancake2 = new Pancake(List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE, ApprovedIngredients.INGREDIENT_HAZELNUTS));
        pancake3 = new Pancake(List.of(ApprovedIngredients.INGREDIENT_WHIPPED_CREAM, ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE));
    }

    @Test
    void testConstructor_WithIngredients() {
        List<String> expected = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM);
        Pancake pancake = new Pancake(expected);
        
        assertEquals(expected, pancake.getIngredients(), "Ingredients should match the provided list");
    }

    @Test
    void testConstructor_Default() {
        Pancake pancake = new Pancake();
        assertTrue(pancake.getIngredients().isEmpty(), "Default constructor should initialize an empty ingredient list");
    }

    @Test
    void testGetIngredients_ReturnsDefensiveCopy() {
        List<String> ingredients = pancake1.getIngredients();
        assertThrows(UnsupportedOperationException.class, () -> ingredients.add(ApprovedIngredients.INGREDIENT_HAZELNUTS), 
            "Modifying returned list should throw an exception");
    }

    @Test
    void testAddNonApprovedIngredient() {
        Pancake pancake = new Pancake();
        pancake.addIngredient("Sugar");
        assertNotEquals(List.of("Sugar"), pancake.getIngredients(), "Ingredient should not be added to the list");
    }

    @Test
    void testEquals_SameIngredients_DifferentOrder() {
        assertTrue(pancake1.equals(pancake3), "Pancakes with the same ingredients in different order should be equal");
    }

    @Test
    void testEquals_DifferentIngredients() {
        assertFalse(pancake1.equals(pancake2), "Pancakes with different ingredients should not be equal");
    }

    @Test
    void testEquals_Null() {
        assertFalse(pancake1.equals(null), "Pancake should not be equal to null");
    }

    @Test
    void testEquals_DifferentObjectType() {
        assertFalse(pancake1.equals("Some String"), "Pancake should not be equal to an unrelated object");
    }

    @Test
    void testEquals_SameReference() {
        assertTrue(pancake1.equals(pancake1), "Pancake should be equal to itself");
    }

    @Test
    void testHashCode_SameIngredients() {
        assertEquals(pancake1.hashCode(), pancake3.hashCode(), "Hash codes should be equal for same ingredients");
    }

    @Test
    void testHashCode_DifferentIngredients() {
        assertNotEquals(pancake1.hashCode(), pancake2.hashCode(), "Hash codes should differ for different ingredients");
    }

    @Test
    void testGetIngredients_IsImmutable() {
        List<String> ingredients = pancake1.getIngredients();
        assertThrows(UnsupportedOperationException.class, () -> ingredients.add("Eggs"), 
            "Returned list should be immutable");
    }
}