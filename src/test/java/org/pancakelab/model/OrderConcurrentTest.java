package org.pancakelab.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

class OrderConcurrentTest {

    private static final int THREAD_COUNT = 10;
    private Order order;
    private static final List<String> SAMPLE_INGREDIENTS = List.of(ApprovedIngredients.INGREDIENT_DARK_CHOCOLATE, ApprovedIngredients.INGREDIENT_HAZELNUTS);

    @BeforeEach
    void setUp() {
        order = new Order(1, 101); // Building 1, Room 101
    }

    @Test
    void testConcurrentAddPancakes() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable task = () -> {
			try {
				order.addPancake(SAMPLE_INGREDIENTS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(THREAD_COUNT, order.getPancakes().size(), "All threads should have added a pancake");
    }

    @Test
    void testConcurrentRemovePancakes() throws InterruptedException {
    	try {
        	for (int i = 0; i < 10; i++) {
    			order.addPancake(SAMPLE_INGREDIENTS);
        	}
    	} catch (Exception e) {
			e.printStackTrace();
		}

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable task = () -> {
			try {
				order.removePancake(pancakeDescrption(SAMPLE_INGREDIENTS));
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertTrue(order.getPancakes().size() <= 1, "Only one pancake should remain or be empty");
    }

    @Test
    void testConcurrentReadWritePancakes() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable writeTask = () -> {
			try {
				order.addPancake(SAMPLE_INGREDIENTS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
        Runnable readTask = () -> assertNotNull(order.getPancakes());

        IntStream.range(0, THREAD_COUNT / 2).forEach(i -> executor.submit(writeTask));
        IntStream.range(0, THREAD_COUNT / 2).forEach(i -> executor.submit(readTask));

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(THREAD_COUNT / 2, order.getPancakes().size(), "Half of the threads should have added pancakes");
    }

    @Test
    void testConcurrentStatusChanges() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable task = () -> {
            try {
				order.complete();
				order.prepare();
	            order.deliver();
			} catch (Exception e) {
				e.printStackTrace();
			}
        };

        IntStream.range(0, THREAD_COUNT).forEach(i -> executor.submit(task));

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertTrue(order.isDelivered(), "The order should be in 'DELIVERED' state after all status changes");
    }

    @Test
    void testConcurrentOperations_NoDeadlocks() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        Runnable addTask = () -> {
			try {
				order.addPancake(SAMPLE_INGREDIENTS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
        Runnable removeTask = () -> {
			try {
				order.removePancake(pancakeDescrption(SAMPLE_INGREDIENTS));
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
        Runnable deliverTask = () -> {
			try {
				order.deliver();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
        Runnable statusTask = () -> {
			try {
				order.complete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

        IntStream.range(0, THREAD_COUNT).forEach(i -> {
            executor.submit(addTask);
            executor.submit(removeTask);
            executor.submit(deliverTask);
            executor.submit(statusTask);
        });

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertTrue(order.isCompleted());
    }
    
    private String pancakeDescrption(List<String> ingredients) {
   	 return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
   }
}

