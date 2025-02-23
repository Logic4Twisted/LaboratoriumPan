package org.pancakelab.model.pancakes;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.pancakelab.model.Order;

public class InMemoryOrderRepository implements OrderRepository {

	private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

	public Optional<Order> findById(UUID orderId) {
		return Optional.ofNullable(orders.get(orderId));
	}

	public void save(Order order) {
		orders.put(order.getId(), order);
	}

	public void delete(UUID orderId) {
		orders.remove(orderId);
	}

	public Collection<Order> findAll() {
		return orders.values();
	}

}
