package org.pancakelab.service;

import java.util.List;

import org.pancakelab.model.OrderInterface;

public class PancakeManagerImpl implements PancakeManager {
	// business constraints
	public static final int MAX_PANCAKE_COUNT = 100;
	public static final int MAX_PANCAKE_PER_ORDER = 500;

	/* Assumption:
	 * Design pattern should be used to avoid hardcoding recipes for pancakes and to allow disciples to choose the ingredients ??
	 */
    public void addPancakes(OrderInterface order, List<String> ingredients, int count) {
        count = Math.min(count, MAX_PANCAKE_COUNT);
        for (int i = 0; i < count && order.getPancakes().size() < MAX_PANCAKE_PER_ORDER; i++) {
        	order.addPancake(ingredients);
        }
        
    }
    
    public void removePancakes(OrderInterface order, String description, int count) {
    	for (int i = 0; i < count; i++) {
            order.removePancake(description);
    	}
    }
}
