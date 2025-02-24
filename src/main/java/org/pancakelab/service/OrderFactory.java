package org.pancakelab.service;

import org.pancakelab.model.OrderInterface;

public interface OrderFactory {
	OrderInterface createOrder(int building, int room);
}
