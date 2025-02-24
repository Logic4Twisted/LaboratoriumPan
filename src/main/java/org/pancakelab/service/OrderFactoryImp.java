package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.OrderInterface;

public class OrderFactoryImp implements OrderFactory {

	@Override
	public OrderInterface createOrder(int building, int room) {
		return new Order(building, room);
	}
}
