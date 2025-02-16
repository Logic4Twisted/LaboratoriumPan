package org.pancakelab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.Order;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PancakeServiceTest {
    private PancakeService pancakeService;
    
    private final static String DARK_CHOCOLATE_PANCAKE_DESCRIPTION           				= "Delicious pancake with dark chocolate!";
    private final static String MILK_CHOCOLATE_PANCAKE_DESCRIPTION           				= "Delicious pancake with milk chocolate!";
    private final static String MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION 				= "Delicious pancake with milk chocolate, hazelnuts!";
    private final static String DARK_CHOCOLATE_WHIPPED_CREAM_PANCAKE_DESCRIPTION 			= "Delicious pancake with dark chocolate, whipped cream!";
    private final static String DARK_CHOCOLATE_WHIPPED_CREAM_HAZELNUTS_PANCAKE_DESCRIPTION 	= "Delicious pancake with dark chocolate, whipped cream, hazelnuts!";
    
    @BeforeEach
    void setUp() {
    	pancakeService = new PancakeService();
    }
    
    @Test
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData_Test() {
        // setup

        // exercise
        UUID orderId = pancakeService.createOrder(10, 20);

        // verify
        assertNotNull(orderId);

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);

        // exercise
        addPancakes(orderId);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderExists_WhenAddingAllPancakes_ThenCorrectPancakesAdded_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);

        // exercise
    	pancakeService.addDarkChocolatePancake(orderId, 1);
    	pancakeService.addDarkChocolateWhippedCreamPancake(orderId, 1);
    	pancakeService.addDarkChocolateWhippedCreamHazelnutsPancake(orderId, 1);
    	pancakeService.addMilkChocolatePancake(orderId, 1);
    	pancakeService.addMilkChocolateHazelnutsPancake(orderId, 1);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             DARK_CHOCOLATE_WHIPPED_CREAM_PANCAKE_DESCRIPTION,
                             DARK_CHOCOLATE_WHIPPED_CREAM_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }

    @Test
    public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	addPancakes(orderId);
    	
        // exercise
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, 2);
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, 3);
        pancakeService.removePancakes(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, orderId, 1);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenPancakesExist_WhenRemovePancakesFromDifferentOrder_ThenCorrectNumberOfPancakes_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	pancakeService.addDarkChocolatePancake(orderId, 1);
    	
    	// exercise
    	pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, UUID.randomUUID(), 1);
    	
    	// verify
    	List<String> orderPancakes = pancakeService.viewOrder(orderId);
    	assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION), orderPancakes);
    }
    
    
    @Test
    public void GivenPancakesExists_WhenRemovingNotAddedPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	pancakeService.addDarkChocolatePancake(orderId, 3);
    	
        // exercise
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, 3);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
        					DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
        					DARK_CHOCOLATE_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenNonExistentOrder_WhenAddingPancakes_ThenNoPancakesAdded_Test() {
        // setup
        UUID invalidOrderId = UUID.randomUUID();

        // exercise
        pancakeService.addDarkChocolatePancake(invalidOrderId, 3);
        
        // verify
        assertTrue(pancakeService.viewOrder(invalidOrderId).isEmpty());
    }
    

    @Test
    public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompletedAndPrepared_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);

        // exercise
        pancakeService.completeOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertTrue(completedOrders.contains(orderId));
        
        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertTrue(preparedOrders.isEmpty());
        // tear down
    }

    
    @Test
    public void GivenOrderCompleted_WhenDeliverignOrder_ThenOrderDelivery_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	addPancakes(orderId);
    	pancakeService.completeOrder(orderId);

        // exercise
        DeliveryResult result = pancakeService.deliverOrder(orderId);

        // verify
        assertFalse(result.isSuccess());
    }
    
    @Test
    public void GivenOrderCompleted_WhenPreparingOrder_ThenOrderPrepared_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	addPancakes(orderId);
    	pancakeService.completeOrder(orderId);

        // exercise
        pancakeService.prepareOrder(orderId);

        // verify
        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertTrue(preparedOrders.contains(orderId));
    }
    
    @Test
    public void GivenOrderCompletedButNotPrepared_WhenDeliveringOrder_ThenOrderNotDelivered_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	addPancakes(orderId);
    	pancakeService.completeOrder(orderId);

        // exercise
        DeliveryResult result = pancakeService.deliverOrder(orderId);

        // verify
        assertFalse(result.isSuccess());
        
        Set<UUID> complatedOrders = pancakeService.listCompletedOrders();
        assertTrue(complatedOrders.contains(orderId));
        
        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));
    }
    

    @Test
    public void GivenOrderPrepared_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
    	addPancakes(orderId);
    	List<String> pancakesToDeliver = new LinkedList<String> (pancakeService.viewOrder(orderId));
    	pancakeService.completeOrder(orderId);
    	pancakeService.prepareOrder(orderId);

        // exercise
        DeliveryResult deliveredOrder = pancakeService.deliverOrder(orderId);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);
        assertEquals(orderId, deliveredOrder.getOrderId());
        assertEquals(pancakesToDeliver, deliveredOrder.getPancakesToDeliver());

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderCompleted_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderPrepared_WhenCancellingOrder_ThenOrderDoesNotExis_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderDelivered_WhenCancellingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);
        pancakeService.deliverOrder(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenCancellingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenCompletingOrderAgain_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        pancakeService.completeOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);
    }
    
    
    @Test
    public void GivenOrderCancelled_WhenPreparingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        pancakeService.prepareOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenDeliveringOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        DeliveryResult result = pancakeService.deliverOrder(orderId);

        // verify
        assertFalse(result.isSuccess());
        
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        List<String> ordersPancakes = pancakeService.viewOrder(orderId);

        assertEquals(List.of(), ordersPancakes);
    }
    
    @Test
    public void GivenNonExistentOrder_WhenCancellingOrder_ThenNothingHappens_Test() {
        // setup
        UUID invalidOrderId = UUID.randomUUID();

        // exercise
        pancakeService.cancelOrder(invalidOrderId);

        // verify (no exception should be thrown, no changes to the system)
        assertFalse(pancakeService.listCompletedOrders().contains(invalidOrderId));
        assertFalse(pancakeService.listPreparedOrders().contains(invalidOrderId));
    }
    
    @Test
    public void GivenOrderWithoutPancakes_WhenCompletingOrder_ThenOrderCompleted_Test() {
        // setup
        UUID orderId = pancakeService.createOrder(10, 20);

        // exercise
        pancakeService.completeOrder(orderId);

        // verify
        assertTrue(pancakeService.listCompletedOrders().contains(orderId));
    }
    
    @Test
    public void GivenOrderNotCompleted_WhenPreparingOrder_ThenOrderNotPrepared_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);

        // exercise
        pancakeService.prepareOrder(orderId);

        // verify
        assertFalse(pancakeService.listPreparedOrders().contains(orderId));
    }
    
    @Test
    public void GivenOrderDelivered_WhenDeliveringAgain_ThenOrderNotFound_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);

        // first delivery
        DeliveryResult firstDelivery = pancakeService.deliverOrder(orderId);

        // exercise
        DeliveryResult secondDelivery = pancakeService.deliverOrder(orderId);

        // verify
        assertTrue(firstDelivery.isSuccess());
        assertFalse(secondDelivery.isSuccess());
    }
    
    @Test
    public void WhenDeliveringUnknownOrder_ThenFailGracefuly_Test() {
    	// exercise
    	DeliveryResult delivery = pancakeService.deliverOrder(UUID.randomUUID());
    	
    	//verify
    	assertFalse(delivery.isSuccess());
    }
    
    @Test
    public void GivenLargeNumberOfPancakes_WhenAddingAndRemoving_ThenSystemHandlesProperly_Test() {
        // setup
        UUID orderId = pancakeService.createOrder(10, 20);
        int largeCount = 10_000;
        
        // exercise
        pancakeService.addDarkChocolatePancake(orderId, largeCount);
        
        // verify
        assertEquals(largeCount, pancakeService.viewOrder(orderId).size());

        // remove half and check again
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, largeCount / 2);
        assertEquals(largeCount / 2, pancakeService.viewOrder(orderId).size());
    }
    
    @Test
    public void GivenNewOrder_WhenProcessedThroughLifecycle_ThenSuccessfullyDelivered_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);

        // exercise
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);
        DeliveryResult deliveredOrder = pancakeService.deliverOrder(orderId);

        // verify
        assertTrue(deliveredOrder.isSuccess());
        assertEquals(orderId, deliveredOrder.getOrderId());
        assertTrue(pancakeService.viewOrder(orderId).isEmpty());
    }
    
    
    @Test
    public void GivenNewOrder_WhenAppendingToDescription_ThenNotUpdated_Test() {
    	// setup
        UUID orderId = pancakeService.createOrder(10, 20);
        addPancakes(orderId);
        pancakeService.completeOrder(orderId);
        
        List<String> expected = new LinkedList<String>();
        for (String desc : pancakeService.viewOrder(orderId)) {
        	expected.add("" + desc);
        }
        for (String desc : pancakeService.viewOrder(orderId)) {
        	desc.replace("Choc", "Xxxx");
        }
        System.out.println(String.join(",", pancakeService.viewOrder(orderId)));
        assertEquals(String.join(",", expected), String.join(",", pancakeService.viewOrder(orderId)));
    }
    
    @Test
    public void testGetAvailableIngredients() {
        PancakeService pancakeService = new PancakeService();

        // Expected ingredients (from the HashSet in PancakeService)
        Set<String> expectedIngredients = Set.of("dark chocolate", "milk chocolate", "whipped cream", "hazelnuts");

        // Get available ingredients
        List<String> availableIngredients = pancakeService.getAvailableIngredients();

        // Check that the list contains all approved ingredients
        assertEquals(expectedIngredients.size(), availableIngredients.size(), "Ingredient list size mismatch!");
        assertTrue(availableIngredients.containsAll(expectedIngredients), "Missing approved ingredients!");

        // Ensure the list is immutable (modifying it doesn't affect the original)
        availableIngredients.add("strawberries"); // This should not modify the actual approved set
        List<String> newAvailableIngredients = pancakeService.getAvailableIngredients();
        assertFalse(newAvailableIngredients.contains("strawberries"), "New ingredient was incorrectly added!");
    }
    

    private void addPancakes(UUID orderId) {
        pancakeService.addDarkChocolatePancake(orderId, 3);
        pancakeService.addMilkChocolatePancake(orderId, 3);
        pancakeService.addMilkChocolateHazelnutsPancake(orderId, 3);
    }
}
