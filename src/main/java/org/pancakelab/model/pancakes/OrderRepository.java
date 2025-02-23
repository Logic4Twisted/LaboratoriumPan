package org.pancakelab.model.pancakes;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.pancakelab.model.OrderInterface;

public interface OrderRepository {
	
	public Optional<OrderInterface> findById(UUID orderId);
	public void save(OrderInterface order);
	public void delete(UUID orderId);
	public Collection<OrderInterface> findAll();
}

