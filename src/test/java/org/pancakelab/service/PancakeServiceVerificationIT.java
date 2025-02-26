package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.*;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PancakeServiceVerificationIT {

    private PancakeService pancakeService;
    private InMemoryOrderRepository orderRepository;
    private PancakeManager pancakeManager;
    private OrderFactory orderFactory;

    private UUID validOrderId;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        pancakeManager = new PancakeManagerImpl();
        orderFactory = new OrderFactoryImp();

        pancakeService = new PancakeService(orderRepository, pancakeManager, orderFactory);

        // Create a sample order
        PancakeOperationResult result = pancakeService.createOrder(1, 101);
        validOrderId = result.getOrderId();
    }

    /** CREATE ORDER TESTS **/
    @Test
    void createOrder_ShouldReturnSuccess_WhenValidBuildingAndRoom() {
        PancakeOperationResult result = pancakeService.createOrder(2, 202);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOrderId());
    }

    @Test
    void createOrder_ShouldReturnFailure_WhenInvalidBuildingOrRoom() {
        PancakeOperationResult result = pancakeService.createOrder(0, 101);

        assertFalse(result.isSuccess());
        assertEquals("Invalid building or room number.", result.getMessage());
    }

    /** ADD PANCAKES TESTS **/
    @Test
    void addPancakes_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.addPancakes(validOrderId, List.of("Chocolate"), 2);

        assertFalse(result.isSuccess());
        assertEquals(validOrderId, result.getOrderId());
    }

    @Test
    void addPancakes_ShouldReturnFailure_WhenOrderIdIsNull() {
        PancakeOperationResult result = pancakeService.addPancakes(null, List.of("Chocolate"), 2);

        assertFalse(result.isSuccess());
        assertEquals("Order ID cannot be null.", result.getMessage());
    }

    /** REMOVE PANCAKES TESTS **/
    @Test
    void removePancakes_ShouldReturnSuccess_WhenOrderExists() {
    	PancakeOperationResult result = pancakeService.addPancakes(validOrderId, List.of("dark chocolate"), 3);
    	assertTrue(result.isSuccess());
        PancakeOperationResult removeResult = pancakeService.removePancakes(pancakeDescrption(List.of("dark chocolate")), validOrderId, 1);

        assertTrue(removeResult.isSuccess());
    }

    /** VIEW ORDER TESTS **/
    @Test
    void viewOrder_ShouldReturnOrderDetails_WhenOrderExists() {
        pancakeService.addPancakes(validOrderId, List.of("Dark Chocolate", "Hazelnuts"), 2);
        ViewOrderResult result = pancakeService.viewOrder(validOrderId);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getPancakes().size());
    }

    @Test
    void viewOrder_ShouldReturnFailure_WhenOrderDoesNotExist() {
        ViewOrderResult result = pancakeService.viewOrder(UUID.randomUUID());

        assertFalse(result.isSuccess());
        assertEquals("Order not found.", result.getMessage());
    }

    /** CANCEL ORDER TESTS **/
    @Test
    void cancelOrder_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.cancelOrder(validOrderId);

        assertTrue(result.isSuccess());
    }
    
    /** CANCEL nonexitings ORDER TESTS **/
    @Test
    void cancelNotExistingOrder_ShouldReturnFailure_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.cancelOrder(UUID.randomUUID());

        assertFalse(result.isSuccess());
    }

    /** COMPLETE ORDER TESTS **/
    @Test
    void completeOrder_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.completeOrder(validOrderId);

        assertTrue(result.isSuccess());
    }
    
    /** Complete nonexitings ORDER TESTS **/
    @Test
    void completeNotExistingOrder_ShouldReturnFailure_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.completeOrder(UUID.randomUUID());

        assertFalse(result.isSuccess());
    }

    /** PREPARE ORDER TESTS **/
    @Test
    void prepareOrder_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.prepareOrder(validOrderId);

        assertFalse(result.isSuccess());
    }
    
    /** PREPARE nonexisting ORDER TESTS **/
    @Test
    void prepareNonexistingOrder_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.prepareOrder(UUID.randomUUID());

        assertFalse(result.isSuccess());
    }

    /** DELIVER ORDER TESTS **/
    @Test
    void deliverOrder_ShouldReturnSuccess_WhenOrderExists() {
        pancakeService.completeOrder(validOrderId);
        pancakeService.prepareOrder(validOrderId);
        DeliveryResult result = pancakeService.deliverOrder(validOrderId);

        assertTrue(result.isSuccess());
        
        DeliveryResult resultNonExisting = pancakeService.deliverOrder(UUID.randomUUID());
        assertFalse(resultNonExisting.isSuccess());
    }
    
    private String pancakeDescrption(List<String> ingredients) {
   	 return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
   }
}

