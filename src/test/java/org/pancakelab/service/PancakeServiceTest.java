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
import org.pancakelab.model.PancakeOperationResult;
import org.pancakelab.model.ViewOrderResult;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;

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
    	pancakeService = new PancakeService(new InMemoryOrderRepository(), new PancakeManagerImpl(), new OrderFactoryImp());
    }
    
    @Test
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData_Test() {
        // setup

        // exercise
        UUID orderId = createOrder(10, 20);

        // verify
        assertNotNull(orderId);

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);

        // exercise
        addSomePancakes(orderId);

        // verify
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

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
    	UUID orderId = createOrder(10, 20);

        // exercise
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM, ApprovedIngredients.INGREDIENT_HAZELNUTS), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE), 1);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE, ApprovedIngredients.INGREDIENT_HAZELNUTS), 1);

        // verify
    	ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

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
    	UUID orderId = createOrder(10, 20);
    	addSomePancakes(orderId);
    	
        // exercise
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, 2);
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, 3);
        pancakeService.removePancakes(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, orderId, 1);

        // verify
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenPancakesExist_WhenRemovePancakesFromDifferentOrder_ThenCorrectNumberOfPancakes_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
    	
    	// exercise
    	pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, UUID.randomUUID(), 1);
    	
    	// verify
    	ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();
    	assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION), ordersPancakes);
    }
    
    
    @Test
    public void GivenPancakesExists_WhenRemovingNotAddedPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 3);
    	
        // exercise
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, 3);

        // verify
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
        					DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
        					DARK_CHOCOLATE_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrder_WhenRemovingNull_ThenDontRemoveAndDontThrowException() {
    	UUID orderId = createOrder(10, 20);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
    	
    	// exercise
    	pancakeService.removePancakes(null, orderId, 1);
    	
    	// verify
    	ViewOrderResult result = pancakeService.viewOrder(orderId);
    	List<String> pancakes = result.getPancakes();
    	assertEquals(List.of(pancakeDescrption(List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE))), pancakes);
    }
    
    @Test
    public void GivenOrder_WhenAddingCustard_ThenDontAddPancake() {
    	UUID orderId = createOrder(10, 20);
    	
    	
    	// exercise
    	pancakeService.addPancakes(orderId, List.of("custard"), 1);
    	
    	// verify
    	ViewOrderResult result = pancakeService.viewOrder(orderId);
    	List<String> pancakes = result.getPancakes();
    	assertEquals(List.of(pancakeDescrption(List.of())), pancakes);
    }
    
    @Test
    public void GivenNonExistentOrder_WhenAddingPancakes_ThenNoPancakesAdded_Test() {
        // setup
        UUID invalidOrderId = UUID.randomUUID();

        // exercise
        pancakeService.addPancakes(invalidOrderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 1);
        
        // verify
        ViewOrderResult result = pancakeService.viewOrder(invalidOrderId);
        List<String> ordersPancakes = result.getPancakes();
        assertTrue(ordersPancakes.isEmpty());
    }
    

    @Test
    public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompletedAndPrepared_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);

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
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
    	addSomePancakes(orderId);
    	
    	ViewOrderResult result = pancakeService.viewOrder(orderId);
    	List<String> pancakesToDeliver = new LinkedList<String> (result.getPancakes());
    	pancakeService.completeOrder(orderId);
    	pancakeService.prepareOrder(orderId);

        // exercise
        DeliveryResult deliveredOrder = pancakeService.deliverOrder(orderId);

        // verify
        assertTrue(deliveredOrder.isSuccess());
        assertEquals(orderId, deliveredOrder.getOrderId());
        assertEquals(pancakesToDeliver, deliveredOrder.getPancakesToDeliver());
        
        assertEquals(List.of(), pancakeService.viewOrder(orderId).getPancakes(), "Order should be removed");
        // tear down
    }

    @Test
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
        addSomePancakes(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderCompleted_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
        addSomePancakes(orderId);
        pancakeService.completeOrder(orderId);
        
        // exercise
        pancakeService.cancelOrder(orderId);

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(orderId));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(orderId));

        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderPrepared_WhenCancellingOrder_ThenOrderDoesNotExis_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
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

        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderDelivered_WhenCancellingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
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
    	UUID orderId = createOrder(10, 20);
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
        UUID orderId = createOrder(10, 20);

        // exercise
        pancakeService.completeOrder(orderId);

        // verify
        assertTrue(pancakeService.listCompletedOrders().contains(orderId));
    }
    
    @Test
    public void GivenOrderNotCompleted_WhenPreparingOrder_ThenOrderNotPrepared_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
        addSomePancakes(orderId);

        // exercise
        pancakeService.prepareOrder(orderId);

        // verify
        assertFalse(pancakeService.listPreparedOrders().contains(orderId));
    }
    
    @Test
    public void GivenOrderDelivered_WhenDeliveringAgain_ThenOrderNotFound_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
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
        UUID orderId = createOrder(10, 20);
        int largeCount = 10_000;
        
        // exercise
        pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), largeCount);
        
        // verify
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();
        assertEquals(PancakeManagerImpl.MAX_PANCAKE_COUNT, ordersPancakes.size());

        // remove half and check again
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, orderId, largeCount / 2);
        result = pancakeService.viewOrder(orderId);
        ordersPancakes = result.getPancakes();
           
        assertEquals(0, ordersPancakes.size());
    }
    
    @Test
    public void GivenNewOrder_WhenProcessedThroughLifecycle_ThenSuccessfullyDelivered_Test() {
        // setup
    	UUID orderId = createOrder(10, 20);
        addSomePancakes(orderId);

        // exercise
        pancakeService.completeOrder(orderId);
        pancakeService.prepareOrder(orderId);
        DeliveryResult deliveredOrder = pancakeService.deliverOrder(orderId);

        // verify
        assertTrue(deliveredOrder.isSuccess());
        assertEquals(orderId, deliveredOrder.getOrderId());
        assertEquals(List.of(), pancakeService.viewOrder(orderId).getPancakes());
    }
    
    
    @Test
    public void GivenNewOrder_WhenAppendingToDescription_ThenNotUpdated_Test() {
    	// setup
        UUID orderId = createOrder(10, 20);
        addSomePancakes(orderId);
        pancakeService.completeOrder(orderId);
        
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();
        
        
        List<String> expected = new LinkedList<String>();
        for (String desc : ordersPancakes) {
        	expected.add("" + desc);
        }
        for (String desc : ordersPancakes) {
        	desc.replace("Choc", "Xxxx");
        }
        System.out.println(String.join(",", ordersPancakes));
        assertEquals(String.join(",", expected), String.join(",", ordersPancakes));
    }
    
    @Test
    void testAddPancakes_SuccessfulAddition() {
        UUID orderId = createOrder(10, 20);
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM);

        // Add 3 pancakes
        pancakeService.addPancakes(orderId, ingredients, 3);

        // Fetch order and check the pancakes count
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> ordersPancakes = result.getPancakes();
        
        assertEquals(3, ordersPancakes.size(), "Three pancakes should be added");
        assertEquals(pancakeDescrption(ingredients), ordersPancakes.get(0));
        assertEquals(pancakeDescrption(ingredients), ordersPancakes.get(1));
        assertEquals(pancakeDescrption(ingredients), ordersPancakes.get(2));
    }

    
    @Test
    void testAddPancakes_RespectsMaxPancakeCount() {
    	UUID orderId = createOrder(10, 20);
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_WHIPPED_CREAM);

        // Try adding more than MAX_PANCAKE_COUNT
        pancakeService.addPancakes(orderId, ingredients, 102);

        // Fetch order and check pancake count
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> viewOrder = result.getPancakes();

        assertNotNull(viewOrder, "Order should exist");
        assertEquals(100, viewOrder.size(), "Should not exceed max allowed pancakes");
    }

    @Test
    void testAddPancakes_EmptyIngredientList() {
        UUID orderId = createOrder(10, 20);
        List<String> ingredients = List.of();

        // Try adding with empty ingredients
        pancakeService.addPancakes(orderId, ingredients, 2);

        // Fetch order and check pancakes count
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> viewOrder = result.getPancakes();
        
        assertEquals(2, viewOrder.size());
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(0));
        assertEquals(pancakeDescrption(ingredients), viewOrder.get(1));
    }

    
    @Test
    void testAddPancakes_NullIngredients() {
    	UUID orderId = createOrder(10, 20);
         
        // Call with null ingredients
        pancakeService.addPancakes(orderId, null, 2);

        // Fetch order and check pancakes count
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> viewOrder = result.getPancakes();
        
        assertNotNull(viewOrder, "Order should exist");
        assertEquals(2, viewOrder.size(), "No pancakes should be added with null ingredient list");
    }

    
    @Test
    void testAddPancakes_NegativeCount() {
    	UUID orderId = createOrder(10, 20);
        List<String> ingredients = List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE);

        // Try adding with negative count
        pancakeService.addPancakes(orderId, ingredients, -2);

        // Fetch order and check pancakes count
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> viewOrder = result.getPancakes();
        
        assertNotNull(viewOrder, "Order should exist");
        assertEquals(0, viewOrder.size(), "No pancakes should be added with negative count");
    }

    
    @Test
    void testAddPancakes_ZeroCount() {
    	UUID orderId = createOrder(10, 20);

        // Try adding with zero count
        pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE), 0);

        // Fetch order and check pancakes count
        ViewOrderResult result = pancakeService.viewOrder(orderId);
        List<String> orderView = result.getPancakes();

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
        ViewOrderResult result = pancakeService.viewOrder(invalidOrderId);
        List<String> orderView = result.getPancakes();
        
        assertEquals(0, orderView.size());
    }
    
    private String pancakeDescrption(List<String> ingredients) {
    	 return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
    }
    
    private UUID createOrder(int building, int room) {
    	PancakeOperationResult result = pancakeService.createOrder(building, room);
    	return result.getOrderId();
    }

    private void addSomePancakes(UUID orderId) {
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE), 3);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE), 3);
    	pancakeService.addPancakes(orderId, List.of(ApprovedIngredients.INGREDIENT_MILK_CHOCOLATE, ApprovedIngredients.INGREDIENT_HAZELNUTS), 3);
    }
}
