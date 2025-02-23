package org.pancakelab.model.pancakes;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.pancakelab.model.Order;
import org.pancakelab.model.OrderInterface;

public class InMemoryOrderRepository implements OrderRepository {

	private final Map<UUID, OrderInterface> orders = new ConcurrentHashMap<>();

	public Optional<OrderInterface> findById(UUID orderId) {
		return Optional.ofNullable(orders.get(orderId));
	}

	public void save(OrderInterface order) {
		orders.put(order.getId(), order);
	}

	public void delete(UUID orderId) {
		orders.remove(orderId);
	}

	public Collection<OrderInterface> findAll() {
		return orders.values();
	}

}
