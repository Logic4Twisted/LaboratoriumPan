package org.pancakelab.model.pancakes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PancakeFactoryTest {

    @Test
    public void GivenValidPancakeType_WhenGettingPancake_ThenSameInstanceReturned_Test() {
        // Get the same type of pancake multiple times
        Pancake pancake1 = PancakeFactory.getPancake("DarkChocolate");
        Pancake pancake2 = PancakeFactory.getPancake("DarkChocolate");

        // Verify that both references point to the same instance (Flyweight pattern)
        assertSame(pancake1, pancake2, "Expected the same instance for DarkChocolate pancakes");
    }

    @Test
    public void GivenDifferentPancakeTypes_WhenGettingPancakes_ThenDifferentInstancesReturned_Test() {
        // Get different types of pancakes
        Pancake darkChocolatePancake = PancakeFactory.getPancake("DarkChocolate");
        Pancake milkChocolatePancake = PancakeFactory.getPancake("MilkChocolate");

        // Verify that different types of pancakes are different instances
        assertNotSame(darkChocolatePancake, milkChocolatePancake, "Expected different instances for different pancake types");
    }

    @Test
    public void GivenInvalidPancakeType_WhenGettingPancake_ThenExceptionThrown_Test() {
        // Verify that an unknown pancake type throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PancakeFactory.getPancake("Strawberry");
        });

        assertEquals("Unknown pancake type: Strawberry", exception.getMessage());
    }

    @Test
    public void GivenMultipleRequestsForDifferentPancakes_WhenGettingPancakes_ThenCorrectDescriptionsReturned_Test() {
        // Get different pancakes
        Pancake darkChocolatePancake = PancakeFactory.getPancake("DarkChocolate");
        Pancake milkChocolatePancake = PancakeFactory.getPancake("MilkChocolate");
        Pancake milkChocolateHazelnutsPancake = PancakeFactory.getPancake("MilkChocolateHazelnuts");

        // Verify descriptions
        assertEquals("Delicious pancake with dark chocolate!", darkChocolatePancake.description());
        assertEquals("Delicious pancake with milk chocolate!", milkChocolatePancake.description());
        assertEquals("Delicious pancake with milk chocolate, hazelnuts!", milkChocolateHazelnutsPancake.description());
    }
}

