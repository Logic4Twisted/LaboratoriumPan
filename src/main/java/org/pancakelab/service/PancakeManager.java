package org.pancakelab.service;

import java.util.List;

import org.pancakelab.model.Order;

public interface PancakeManager {
	public void addPancakes(Order order, List<String> ingredients, int count);
	public void removePancakes(Order order, String description, int count);
}
