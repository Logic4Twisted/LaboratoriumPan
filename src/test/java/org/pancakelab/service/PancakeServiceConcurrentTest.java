package org.pancakelab.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.pancakes.Pancake;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

class PancakeServiceConcurrentTest {

    private PancakeService pancakeService;
    private static final int THREAD_COUNT = 40;

    @BeforeEach
    void setUp() {
        pancakeService = new PancakeService();
    }

    @Test
    void testConcurrentOrderCreation() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Set<UUID> orderIds = Collections.synchronizedSet(new HashSet<>());

        Runnable task = () -> {
            UUID orderId = pancakeService.createOrder(1, 101);
            orderIds.add(orderId);
        };

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(THREAD_COUNT, orderIds.size(), "All orders should be uniquely created");
    }

    @Test
    void testConcurrentPancakeAddition() throws InterruptedException {
        UUID orderId = pancakeService.createOrder(1, 101);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable task = () -> pancakeService.addPancakes(orderId, List.of(Pancake.INGREDIENT_DARK_CHOCOLATE), 5);

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        List<String> pancakes = pancakeService.viewOrder(orderId);
        assertEquals(THREAD_COUNT * 5, pancakes.size(), "Total pancakes should match concurrent additions");
    }

    @Test
    void testConcurrentPancakeRemoval() throws InterruptedException {
        UUID orderId = pancakeService.createOrder(1, 101);
        pancakeService.addPancakes(orderId, List.of(Pancake.INGREDIENT_DARK_CHOCOLATE), 100); // Pre-fill pancakes

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Runnable task = () -> pancakeService.removePancakes(description(List.of(Pancake.INGREDIENT_DARK_CHOCOLATE)), orderId, 2);

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        List<String> pancakes = pancakeService.viewOrder(orderId);
        assertEquals(100 - 2*THREAD_COUNT, pancakes.size() , "Total pancakes should be reduced correctly");
    }
    
    @Test
    void testConcurrentPancakeAdditionAndRemove() throws InterruptedException {
        UUID orderId = pancakeService.createOrder(1, 101);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable task = () -> {
        	pancakeService.addPancakes(orderId, List.of(Pancake.INGREDIENT_DARK_CHOCOLATE) , 5);
        	pancakeService.removePancakes(description(List.of(Pancake.INGREDIENT_DARK_CHOCOLATE)), orderId, 4);
        };

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        List<String> pancakes = pancakeService.viewOrder(orderId);
        assertEquals(THREAD_COUNT, pancakes.size(), "Total pancakes should match concurrent additions");
    }

    @Test
    void testConcurrentOrderCancellation() throws InterruptedException {
        List<UUID> orderIds = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
        	UUID orderId = pancakeService.createOrder(1, 101);
            orderIds.add(orderId);
            pancakeService.addPancakes(orderId, List.of(Pancake.INGREDIENT_DARK_CHOCOLATE),  1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Runnable task = () -> pancakeService.cancelOrder(orderIds.get(new Random().nextInt(orderIds.size())));

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long remainingOrders = orderIds.stream().filter(id -> pancakeService.viewOrder(id).size() > 0).count();
        assertTrue(remainingOrders < THREAD_COUNT, "Some orders should be canceled");
    }
    
    private String description(List<String> ingredients) {
    	return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
    }
}