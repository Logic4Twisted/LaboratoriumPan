package org.pancakelab.service;

import java.util.List;

import org.pancakelab.model.OrderInterface;

public interface PancakeManager {
	public void addPancakes(OrderInterface order, List<String> ingredients, int count);
	public void removePancakes(OrderInterface order, String description, int count);
	public void cancel(OrderInterface order);
	public void complete(OrderInterface order);
	public void deliver(OrderInterface order);
	public void prepare(OrderInterface order);
}
