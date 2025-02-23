package org.pancakelab.model.pancakes;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.pancakelab.model.Order;

public interface OrderRepository {
	
	public Optional<Order> findById(UUID orderId);
	public void save(Order order);
	public void delete(UUID orderId);
	public Collection<Order> findAll();
}

