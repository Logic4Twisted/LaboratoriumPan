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
import org.pancakelab.model.Order;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PancakeServiceTest {
    private PancakeService pancakeService;
    
    private final static String DARK_CHOCOLATE_PANCAKE_DESCRIPTION           = "Delicious pancake with dark chocolate!";
    private final static String MILK_CHOCOLATE_PANCAKE_DESCRIPTION           = "Delicious pancake with milk chocolate!";
    private final static String MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate, hazelnuts!";
    
    @BeforeEach
    void setUp() {
    	pancakeService = new PancakeService();
    }
    
    @Test
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData_Test() {
        // setup

        // exercise
        Order order = pancakeService.createOrder(10, 20);

        // verify
        assertEquals(10, order.getBuilding());
        assertEquals(20, order.getRoom());

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);

        // exercise
        addPancakes(order);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

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
    public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
    	addPancakes(order);
    	
        // exercise
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 2);
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 3);
        pancakeService.removePancakes(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, order.getId(), 1);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }
    
    
    @Test
    public void GivenPancakesExists_WhenRemovingNotAddedPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
    	pancakeService.addDarkChocolatePancake(order.getId(), 3);
    	
        // exercise
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 3);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

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
    	Order order = pancakeService.createOrder(10, 20);

        // exercise
        pancakeService.completeOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertTrue(completedOrders.contains(order.getId()));
        
        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertTrue(preparedOrders.isEmpty());
        // tear down
    }

    
    @Test
    public void GivenOrderCompleted_WhenDeliverignOrder_ThenOrderDelivery_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
    	addPancakes(order);
    	pancakeService.completeOrder(order.getId());

        // exercise
        Object[] result = pancakeService.deliverOrder(order.getId());

        // verify
        assertNull(result);
    }
    
    @Test
    public void GivenOrderCompleted_WhenPreparingOrder_ThenOrderPrepared_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
    	addPancakes(order);
    	pancakeService.completeOrder(order.getId());

        // exercise
        pancakeService.prepareOrder(order.getId());

        // verify
        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertTrue(preparedOrders.contains(order.getId()));
    }
    
    @Test
    public void GivenOrderCompletedButNotPrepared_WhenDeliveringOrder_ThenOrderNotDelivered_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
    	addPancakes(order);
    	pancakeService.completeOrder(order.getId());

        // exercise
        Object[] result = pancakeService.deliverOrder(order.getId());

        // verify
        assertNull(result);
        
        Set<UUID> complatedOrders = pancakeService.listCompletedOrders();
        assertTrue(complatedOrders.contains(order.getId()));
        
        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));
    }
    

    @Test
    public void GivenOrderPrepared_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
    	addPancakes(order);
    	List<String> pancakesToDeliver = new LinkedList<String> (pancakeService.viewOrder(order.getId()));
    	pancakeService.completeOrder(order.getId());
    	pancakeService.prepareOrder(order.getId());

        // exercise
        Object[] deliveredOrder = pancakeService.deliverOrder(order.getId());

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);
        assertEquals(order.getId(), ((Order) deliveredOrder[0]).getId());
        assertEquals(pancakesToDeliver, (List<String>) deliveredOrder[1]);

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        
        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderCompleted_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        
        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderPrepared_WhenCancellingOrder_ThenOrderDoesNotExis_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.prepareOrder(order.getId());
        
        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }
    
    @Test
    public void GivenOrderDelivered_WhenCancellingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.prepareOrder(order.getId());
        pancakeService.deliverOrder(order.getId());
        
        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenCancellingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.cancelOrder(order.getId());
        
        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenCompletingOrderAgain_ThenOrderDoesNotExist_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.cancelOrder(order.getId());
        
        // exercise
        pancakeService.completeOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);
    }
    
    
    @Test
    public void GivenOrderCancelled_WhenPreparingOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.cancelOrder(order.getId());
        
        // exercise
        pancakeService.prepareOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);
       
    }
    
    @Test
    public void GivenOrderCancelled_WhenDeliveringOrder_ThenOrderDoesNotExist_Test() {
        // setup
    	Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.prepareOrder(order.getId());
        pancakeService.cancelOrder(order.getId());
        
        // exercise
        Object[] result = pancakeService.deliverOrder(order.getId());

        // verify
        assertNull(result);
        
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

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
        Order order = pancakeService.createOrder(10, 20);

        // exercise
        pancakeService.completeOrder(order.getId());

        // verify
        assertTrue(pancakeService.listCompletedOrders().contains(order.getId()));
    }
    
    @Test
    public void GivenOrderNotCompleted_WhenPreparingOrder_ThenOrderNotPrepared_Test() {
        // setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);

        // exercise
        pancakeService.prepareOrder(order.getId());

        // verify
        assertFalse(pancakeService.listPreparedOrders().contains(order.getId()));
    }
    
    @Test
    public void GivenOrderDelivered_WhenDeliveringAgain_ThenOrderNotFound_Test() {
        // setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        pancakeService.prepareOrder(order.getId());

        // first delivery
        Object[] firstDelivery = pancakeService.deliverOrder(order.getId());

        // exercise
        Object[] secondDelivery = pancakeService.deliverOrder(order.getId());

        // verify
        assertNotNull(firstDelivery);
        assertNull(secondDelivery);
    }
    
    @Test
    public void GivenLargeNumberOfPancakes_WhenAddingAndRemoving_ThenSystemHandlesProperly_Test() {
        // setup
        Order order = pancakeService.createOrder(10, 20);
        int largeCount = 10_000;
        
        // exercise
        pancakeService.addDarkChocolatePancake(order.getId(), largeCount);
        
        // verify
        assertEquals(largeCount, pancakeService.viewOrder(order.getId()).size());

        // remove half and check again
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), largeCount / 2);
        assertEquals(largeCount / 2, pancakeService.viewOrder(order.getId()).size());
    }
    
    @Test
    public void GivenNewOrder_WhenProcessedThroughLifecycle_ThenSuccessfullyDelivered_Test() {
        // setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);

        // exercise
        pancakeService.completeOrder(order.getId());
        pancakeService.prepareOrder(order.getId());
        Object[] deliveredOrder = pancakeService.deliverOrder(order.getId());

        // verify
        assertNotNull(deliveredOrder);
        assertEquals(order.getId(), ((Order) deliveredOrder[0]).getId());
        assertTrue(pancakeService.viewOrder(order.getId()).isEmpty());
    }
    
    
    @Test
    public void GivenNewOrder_WhenAppendingToDescription_ThenNotUpdated_Test() {
    	// setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.completeOrder(order.getId());
        
        List<String> expected = new LinkedList<String>();
        for (String desc : pancakeService.viewOrder(order.getId())) {
        	expected.add("" + desc);
        }
        for (String desc : pancakeService.viewOrder(order.getId())) {
        	desc.replace("Choc", "Xxxx");
        }
        System.out.println(String.join(",", pancakeService.viewOrder(order.getId())));
        assertEquals(String.join(",", expected), String.join(",", pancakeService.viewOrder(order.getId())));
    }
    

    private void addPancakes(Order order) {
        pancakeService.addDarkChocolatePancake(order.getId(), 3);
        pancakeService.addMilkChocolatePancake(order.getId(), 3);
        pancakeService.addMilkChocolateHazelnutsPancake(order.getId(), 3);
    }
}
