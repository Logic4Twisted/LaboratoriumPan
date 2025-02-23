package org.pancakelab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.pancakelab.model.ApprovedIngredients;
import org.pancakelab.model.DeliveryResult;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;
import org.pancakelab.model.pancakes.Pancake;

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
    	pancakeService = new PancakeService(new InMemoryOrderRepository(), new PancakeManager());
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
        addSomePancakes(orderId);

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
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM, ApprovedIngredients.INGREDIENT_HAZELNUTS), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE, ApprovedIngredients.INGREDIENT_HAZELNUTS), 1);

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
    	addSomePancakes(orderId);
    	
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
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
    	
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
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 3);
    	
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
    public void GivenOrder_WhenRemovingNull_ThenDontRemoveAndDontThrowException() {
    	UUID orderId = pancakeService.createOrder(10, 20);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
    	
    	// exercise
    	pancakeService.removePancakes(null, orderId, 1);
    	
    	// verify
    	assertEquals(List.of(pancakeDescrption(List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE))), pancakeService.viewOrder(orderId));
    }
    
    @Test
    public void GivenOrder_WhenAddingCustard_ThenDontAddPancake() {
    	UUID orderId = pancakeService.createOrder(10, 20);
    	
    	
    	// exercise
    	pancakeService.addPancakes(orderId, List.of("custard"), 1);
    	
    	// verify
    	assertEquals(List.of(pancakeDescrption(List.of())), pancakeService.viewOrder(orderId));
    }
    
    @Test
    public void GivenNonExistentOrder_WhenAddingPancakes_ThenNoPancakesAdded_Test() {
        // setup
        UUID invalidOrderId = UUID.randomUUID();

        // exercise
        pancakeService.addPancakes(invalidOrderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
        
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
    	addSomePancakes(orderId);
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
    	addSomePancakes(orderId);
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
    	addSomePancakes(orderId);
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
    	addSomePancakes(orderId);
    	List<String> pancakesToDeliver = new LinkedList<String> (pancakeService.viewOrder(orderId));
    	pancakeService.completeOrder(orderId);
    	pancakeService.prepareOrder(orderId);

        // exercise
        DeliveryResult deliveredOrder = pancakeService.deliverOrder(orderId);

        // verify
        assertTrue(deliveredOrder.isSuccess());
        assertEquals(orderId, deliveredOrder.getOrderId());
        assertEquals(pancakesToDeliver, deliveredOrder.getPancakesToDeliver());
        
        assertEquals(List.of(), pancakeService.viewOrder(orderId), "Order should be removed");
        // tear down
    }

    @Test
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
        
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
        addSomePancakes(orderId);
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
        addSomePancakes(orderId);
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
        addSomePancakes(orderId);
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
    }
    
    @Test
    public void GivenOrderCancelled_WhenCancellingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenCompletingOrderAgain_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        pancakeService.completeOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));
    }
    
    
    @Test
    public void GivenOrderCancelled_WhenPreparingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
        pancakeService.completeOrder(orderId);
        pancakeService.cancelOrder(orderId);
        
        // exercise
        pancakeService.prepareOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenDeliveringOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
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
        addSomePancakes(orderId);

        // exercise
        pancakeService.prepareOrder(orderId);

        // verify
        assertFalse(pancakeService.listPreparedOrders().contains(orderId));
    }
    
    @Test
    public void GivenOrderDelivered_WhenDeliveringAgain_ThenOrderNotFound_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
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
        pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), largeCount);
        
        // verify
        assertEquals(PancakeManager.MAX_PANCAKE_COUNT, pancakeService.viewOrder(orderId).size());

        // remove half and check again
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, largeCount / 2);
        assertEquals(0, pancakeService.viewOrder(orderId).size());
    }
    
    @Test
    public void GivenNewOrder_WhenProcessedThroughLifecycle_ThenSuccessfullyDelivered_Test() {
        // setup
    	UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);

        // exercise
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);
        DeliveryResult deliveredOrder = pancakeService.deliverOrder(orderId);

        // verify
        assertTrue(deliveredOrder.isSuccess());
        assertEquals(orderId, deliveredOrder.getOrderId());
        assertEquals(pancakeService.viewOrder(orderId), List.of());
    }
    
    
    @Test
    public void GivenNewOrder_WhenAppendingToDescription_ThenNotUpdated_Test() {
    	// setup
        UUID orderId = pancakeService.createOrder(10, 20);
        addSomePancakes(orderId);
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
    void testAddPancakes_SuccessfulAddition() {
        UUID orderId = pancakeService.createOrder(10, 20);
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM);

        // Add 3 pancakes
        pancakeService.addPancakes(orderId, ingredients, 3);

        // Fetch order and check the pancakes count
        List<String> viewOrder = pancakeService.viewOrder(orderId);
        assertEquals(3, viewOrder.size(), "Three pancakes should be added");
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(0));
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(1));
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(2));
    }

    
    @Test
    void testAddPancakes_RespectsMaxPancakeCount() {
    	UUID orderId = pancakeService.createOrder(10, 20);
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM);

        // Try adding more than MAX_PANCAKE_COUNT
        pancakeService.addPancakes(orderId, ingredients, 102);

        // Fetch order and check pancake count
        List<String> viewOrder = pancakeService.viewOrder(orderId);
        assertNotNull(viewOrder, "Order should exist");
        assertEquals(100, viewOrder.size(), "Should not exceed max allowed pancakes");
    }

    @Test
    void testAddPancakes_EmptyIngredientList() {
        UUID orderId = pancakeService.createOrder(10, 20);
        List<String> ingredients = List.of();

        // Try adding with empty ingredients
        pancakeService.addPancakes(orderId, ingredients, 2);

        // Fetch order and check pancakes count
        List<String> viewOrder = pancakeService.viewOrder(orderId);
        assertEquals(2, viewOrder.size());
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(0));
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(1));
    }

    
    @Test
    void testAddPancakes_NullIngredients() {
    	UUID orderId = pancakeService.createOrder(10, 20);
         
        // Call with null ingredients
        pancakeService.addPancakes(orderId, null, 2);

        // Fetch order and check pancakes count
        List<String> viewOrder = pancakeService.viewOrder(orderId);
        assertNotNull(viewOrder, "Order should exist");
        assertEquals(2, viewOrder.size(), "No pancakes should be added with null ingredient list");
    }

    
    @Test
    void testAddPancakes_NegativeCount() {
    	UUID orderId = pancakeService.createOrder(10, 20);
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE);

        // Try adding with negative count
        pancakeService.addPancakes(orderId, ingredients, -2);

        // Fetch order and check pancakes count
        List<String> orderView = pancakeService.viewOrder(orderId);
        assertNotNull(orderView, "Order should exist");
        assertEquals(0, orderView.size(), "No pancakes should be added with negative count");
    }

    
    @Test
    void testAddPancakes_ZeroCount() {
    	UUID orderId = pancakeService.createOrder(10, 20);

        // Try adding with zero count
        pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE), 0);

        // Fetch order and check pancakes count
        List<String> orderView = pancakeService.viewOrder(orderId);
        assertNotNull(orderView, "Order should exist");
        assertEquals(0, orderView.size(), "No pancakes should be added with negative count");
    }

    @Test
    void testAddPancakes_OrderNotFound() {
        UUID invalidOrderId = UUID.randomUUID();
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE);

        // Call method with a non-existent order
        pancakeService.addPancakes(invalidOrderId, ingredients, 2);

        // Ensure no exception is thrown, and order remains non-existent
        List<String> orderView = pancakeService.viewOrder(invalidOrderId);
        assertEquals(0, orderView.size());
    }
    
    private String pancakeDescrption(List<String> ingredients) {
    	 return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
    }

    private void addSomePancakes(UUID orderId) {
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 3);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE), 3);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE, ApprovedIngredients.INGREDIENT_HAZELNUTS), 3);
    }
}
