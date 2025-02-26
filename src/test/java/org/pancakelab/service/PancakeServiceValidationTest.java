package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.*;
import org.pancakelab.model.pancakes.InMemoryOrderRepository;
import org.pancakelab.model.pancakes.PancakeBuilderFactory;
import org.pancakelab.model.pancakes.PancakeBuilderFactoryImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PancakeServiceValidationTest {

    private PancakeService pancakeService;
    private InMemoryOrderRepository orderRepository;
    private PancakeManager pancakeManager;
    private OrderFactory orderFactory;
    private PancakeBuilderFactory pancakeBuilderFactory;

    private UUID validOrderId;

    @BeforeEach
    void setUp() {
        pancakeBuilderFactory = new PancakeBuilderFactoryImpl();
        orderRepository = new InMemoryOrderRepository();
        pancakeManager = new PancakeManagerImpl(pancakeBuilderFactory);
        orderFactory = new OrderFactoryImp();

        pancakeService = new PancakeService(orderRepository, pancakeManager, orderFactory);

        // Create a sample order
        PancakeOperationResult result = pancakeService.createOrder(1, 101);
        validOrderId = result.getOrderId();
    }

    /** CREATE ORDER TESTS **/
    @Test
    void createOrder_ShouldReturnSuccess_WhenBuildingAndRoomAreValid() {
        PancakeOperationResult result = pancakeService.createOrder(2, 202);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOrderId());
    }

    @Test
    void createOrder_ShouldReturnFailure_WhenBuildingOrRoomIsInvalid() {
        PancakeOperationResult result = pancakeService.createOrder(0, 101);

        assertFalse(result.isSuccess());
        assertEquals("Invalid building or room number.", result.getMessage());
    }

    /** ADD PANCAKES TESTS **/
    @Test
    void addPancakes_ShouldReturnFailure_WhenIngredientIsValid() {
        PancakeOperationResult result = pancakeService.addPancakes(validOrderId, List.of("dark chocolate"), 2);

        assertTrue(result.isSuccess());
        assertEquals("", result.getMessage());
    }
    
    @Test
    void addPancakes_ShouldReturnFailure_WhenIngredientIsInvalid() {
        PancakeOperationResult result = pancakeService.addPancakes(validOrderId, List.of("plastic"), 2);

        assertFalse(result.isSuccess());
        assertEquals("Ingredient invalid value", result.getMessage());
    }

    @Test
    void addPancakes_ShouldReturnFailure_WhenOrderIdIsNull() {
        PancakeOperationResult result = pancakeService.addPancakes(null, List.of("Dark Chocolate"), 2);

        assertFalse(result.isSuccess());
        assertEquals("Order ID cannot be null.", result.getMessage());
    }
    
    @Test
    void addPancakes_ShouldReturnFailure_WhenOrderIsInvalid() {
        PancakeOperationResult result = pancakeService.addPancakes(UUID.randomUUID(),  List.of("Dark Chocolate"), 2);

        assertFalse(result.isSuccess());
        assertEquals("Order not found.", result.getMessage());
    }

    /** REMOVE PANCAKES TESTS **/
    @Test
    void removePancakes_ShouldReturnSuccess_WhenPancakeExistsInOrder() {
        PancakeOperationResult addResult = pancakeService.addPancakes(validOrderId, List.of("Dark Chocolate"), 3);
        assertTrue(addResult.isSuccess());

        PancakeOperationResult removeResult = pancakeService.removePancakes(pancakeDescription(List.of("Dark Chocolate")), validOrderId, 1);
        assertTrue(removeResult.isSuccess());
    }
    
    @Test
    void removePancakes_ShouldReturnFalse_WhenOrderisIvalid() {
        PancakeOperationResult result = pancakeService.addPancakes(UUID.randomUUID(), List.of("Dark Chocolate"), 3);
        assertFalse(result.isSuccess());
        assertEquals("Order not found.", result.getMessage());
    }
    
    @Test
    void removePancakes_ShouldReturnFalse_WhenOrderisNull() {
        PancakeOperationResult result = pancakeService.addPancakes(null, List.of("Dark Chocolate"), 3);
        assertFalse(result.isSuccess());
        assertEquals("Order ID cannot be null.", result.getMessage());
    }
    

    /** VIEW ORDER TESTS **/
    @Test
    void viewOrder_ShouldReturnPancakeList_WhenOrderExists() {
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
    
    @Test
    void viewOrder_ShouldReturnFailure_WhenOrderNull() {
        ViewOrderResult result = pancakeService.viewOrder(null);

        assertFalse(result.isSuccess());
        assertEquals("Order ID cannot be null.", result.getMessage());
    }

    /** CANCEL ORDER TESTS **/
    @Test
    void cancelOrder_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.cancelOrder(validOrderId);
        assertTrue(result.isSuccess());
    }

    @Test
    void cancelOrder_ShouldReturnFailure_WhenOrderDoesNotExist() {
        PancakeOperationResult result = pancakeService.cancelOrder(UUID.randomUUID());
        assertFalse(result.isSuccess());
    }
    
    @Test
    void cancelOrder_ShouldReturnFailure_WhenOrderNull() {
        PancakeOperationResult result = pancakeService.cancelOrder(null);
        assertFalse(result.isSuccess());
    }

    /** COMPLETE ORDER TESTS **/
    @Test
    void completeOrder_ShouldReturnSuccess_WhenOrderExists() {
        PancakeOperationResult result = pancakeService.completeOrder(validOrderId);
        assertTrue(result.isSuccess());
    }

    @Test
    void completeOrder_ShouldReturnFailure_WhenOrderDoesNotExist() {
        PancakeOperationResult result = pancakeService.completeOrder(UUID.randomUUID());
        assertFalse(result.isSuccess());
    }
    
    @Test
    void completeOrder_ShouldReturnFailure_WhenOrderNull() {
        PancakeOperationResult result = pancakeService.completeOrder(null);
        assertFalse(result.isSuccess());
    }

    /** PREPARE ORDER TESTS **/
    @Test
    void prepareOrder_ShouldReturnSuccess_WhenOrderExists() {
        pancakeService.completeOrder(validOrderId);
        PancakeOperationResult result = pancakeService.prepareOrder(validOrderId);
        assertTrue(result.isSuccess());
    }

    @Test
    void prepareOrder_ShouldReturnFailure_WhenOrderDoesNotExist() {
        PancakeOperationResult result = pancakeService.prepareOrder(UUID.randomUUID());
        assertFalse(result.isSuccess());
    }
    
    @Test
    void prepareOrder_ShouldReturnFailure_WhenOrderNull() {
        PancakeOperationResult result = pancakeService.prepareOrder(null);
        assertFalse(result.isSuccess());
    }

    /** DELIVER ORDER TESTS **/
    @Test
    void deliverOrder_ShouldReturnSuccess_WhenOrderExistsAndIsReady() {
        pancakeService.completeOrder(validOrderId);
        pancakeService.prepareOrder(validOrderId);
        DeliveryResult result = pancakeService.deliverOrder(validOrderId);

        assertTrue(result.isSuccess());
    }

    @Test
    void deliverOrder_ShouldReturnFailure_WhenOrderDoesNotExist() {
        DeliveryResult result = pancakeService.deliverOrder(UUID.randomUUID());
        assertFalse(result.isSuccess());
    }
    
    @Test
    void deliverOrder_ShouldReturnFailure_WhenOrderNull() {
        DeliveryResult result = pancakeService.deliverOrder(null);
        assertFalse(result.isSuccess());
    }

    private String pancakeDescription(List<String> ingredients) {
        return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
    }
}
