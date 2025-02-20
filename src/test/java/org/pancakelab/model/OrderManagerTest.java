package org.pancakelab.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.pancakes.PancakeBuilder;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.service.PancakeService;

import java.util.List;

class OrderManagerTest {

    private Order order;
    private PancakeRecipe pancake1;
    private PancakeRecipe pancake2;

    @BeforeEach
    void setUp() {
        order = new Order(1,2);
        PancakeBuilder builder = new PancakeBuilder();
        pancake1 = builder.addIngredient(PancakeService.INGREDIENT_DARK_CHOCOLATE).build();
        builder = new PancakeBuilder();
        pancake2 = builder.addIngredient(PancakeService.INGREDIENT_MILK_CHOCOLATE).build();
    }

    @Test
    void testGetPancakeRecipes_EmptyList() {
        List<PancakeRecipe> recipes = order.getPancakes();
        assertTrue(recipes.isEmpty(), "Pancake list should initially be empty");
    }

    @Test
    void testAddPancake() {
        order.addPancake(pancake1);
        List<PancakeRecipe> recipes = order.getPancakes();

        assertEquals(1, recipes.size(), "List should contain one pancake recipe");
        assertTrue(recipes.contains(pancake1), "List should contain the added pancake");
    }

    @Test
    void testRemovePancake_Existing() {
        order.addPancake(pancake1);
        order.addPancake(pancake2);

        order.removePancake(pancake1);
        List<PancakeRecipe> recipes = order.getPancakes();

        assertEquals(1, recipes.size(), "List should contain only one pancake after removal");
        assertFalse(recipes.contains(pancake1), "Removed pancake should not be in the list");
    }

    @Test
    void testRemovePancake_NonExisting() {
        order.addPancake(pancake1);

        PancakeBuilder builder = new PancakeBuilder();
        PancakeRecipe nonExisting = builder
        		.addIngredient(PancakeService.INGREDIENT_HAZELNUTS)
        		.addIngredient(PancakeService.INGREDIENT_WHIPPED_CREAM).build();
        order.removePancake(nonExisting);

        List<PancakeRecipe> recipes = order.getPancakes();
        assertEquals(1, recipes.size(), "List should still contain one pancake");
        assertTrue(recipes.contains(pancake1), "Original pancake should still be in the list");
    }

    @Test
    void testGetPancakeRecipes_ModificationDoesNotAffectOriginalList() {
        order.addPancake(pancake1);
        List<PancakeRecipe> recipes = order.getPancakes();
        recipes.add(pancake2); // Try modifying the returned list

        List<PancakeRecipe> originalList = order.getPancakes();
        assertEquals(1, originalList.size(), "Modifications to the returned list should not affect the original");
    }
}

