package org.pancakelab.model.pancakes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PancakeFactoryTest {

    @Test
    public void GivenValidPancakeType_WhenGettingPancake_ThenSameInstanceReturned_Test() {
        // Get the same type of pancake multiple times
        Pancake pancake1 = PancakeFactory.getPancake("DarkChocolatePancake");
        Pancake pancake2 = PancakeFactory.getPancake("DarkChocolatePancake");

        // Verify that both references point to the same instance (Flyweight pattern)
        assertNotSame(pancake1, pancake2, "Expected different instance for DarkChocolatePancake pancakes");
        assertSame(pancake1.getType(), pancake2.getType(), "Expect the same type for DarkChocolatePancake");
    }

    @Test
    public void GivenDifferentPancakeTypes_WhenGettingPancakes_ThenDifferentInstancesReturned_Test() {
        // Get different types of pancakes
        Pancake darkChocolatePancake = PancakeFactory.getPancake("DarkChocolatePancake");
        Pancake milkChocolatePancake = PancakeFactory.getPancake("MilkChocolatePancake");

        // Verify that different types of pancakes are different instances
        assertNotSame(darkChocolatePancake, milkChocolatePancake, "Expected different instances for different pancake types");
    }

    @Test
    public void GivenInvalidPancakeType_WhenGettingPancake_ThenExceptionThrown_Test() {
        // Verify that an unknown pancake type throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PancakeFactory.getPancake("StrawberryPancake");
        });

        assertEquals("Unknown pancake type: StrawberryPancake", exception.getMessage());
    }

    @Test
    public void GivenMultipleRequestsForDifferentPancakes_WhenGettingPancakes_ThenCorrectDescriptionsReturned_Test() {
        // Get different pancakes
        Pancake darkChocolatePancake = PancakeFactory.getPancake("DarkChocolatePancake");
        Pancake milkChocolatePancake = PancakeFactory.getPancake("MilkChocolatePancake");
        Pancake milkChocolateHazelnutsPancake = PancakeFactory.getPancake("MilkChocolateHazelnutsPancake");

        // Verify descriptions
        assertEquals("Delicious pancake with dark chocolate!", darkChocolatePancake.description());
        assertEquals("Delicious pancake with milk chocolate!", milkChocolatePancake.description());
        assertEquals("Delicious pancake with milk chocolate, hazelnuts!", milkChocolateHazelnutsPancake.description());
    }
}

