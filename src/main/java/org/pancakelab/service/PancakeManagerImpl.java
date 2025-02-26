package org.pancakelab.service;

import java.util.List;

import org.pancakelab.model.ApprovedIngredients;
import org.pancakelab.model.OrderInterface;

public class PancakeManagerImpl implements PancakeManager {
	// business constraints
	public static final int MAX_PANCAKE_COUNT = 100;
	public static final int MAX_PANCAKE_PER_ORDER = 500;

	/* Assumption:
	 * Design pattern should be used to avoid hardcoding recipes for pancakes and to allow disciples to choose the ingredients ??
	 */
	@Override
    public void addPancakes(OrderInterface order, List<String> ingredients, int count) throws Exception {
        for (String ingredient : ingredients) {
        	if (ingredient == null || !ApprovedIngredients.isApproved(ingredient)) {
        		throw new Exception("Ingredient invalid value.");
        	}
        }
        
        count = Math.min(count, MAX_PANCAKE_COUNT);
        for (int i = 0; i < count && order.getPancakes().size() < MAX_PANCAKE_PER_ORDER; i++) {
        	order.addPancake(ingredients);
        }
        
    }
    
    @Override
    public void removePancakes(OrderInterface order, String description, int count) throws Exception {
    	for (int i = 0; i < count; i++) {
            order.removePancake(description);
    	}
    }
    
    @Override
    public void cancel(OrderInterface order) throws Exception {
    	order.cancel();
    }

	@Override
	public void complete(OrderInterface order) throws Exception {
		order.complete();
	}

	@Override
	public void deliver(OrderInterface order) throws Exception {
		order.deliver();
	}

	@Override
	public void prepare(OrderInterface order) throws Exception {
		order.prepare();
	}
	
	
}
