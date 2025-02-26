package org.pancakelab.service;

import java.util.List;

import org.pancakelab.model.OrderInterface;

public interface PancakeManager {
	public void addPancakes(OrderInterface order, List<String> ingredients, int count) throws Exception;
	public void removePancakes(OrderInterface order, String description, int count)  throws Exception;
	public void cancel(OrderInterface order)  throws Exception;
	public void complete(OrderInterface order)  throws Exception;
	public void deliver(OrderInterface order)  throws Exception;
	public void prepare(OrderInterface order)  throws Exception;
}
