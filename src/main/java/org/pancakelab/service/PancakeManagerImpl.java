package org.pancakelab.service;

import java.util.List;

import org.pancakelab.model.OrderInterface;
import org.pancakelab.model.pancakes.PancakeBuilder;
import org.pancakelab.model.pancakes.PancakeBuilderFactory;
import org.pancakelab.model.pancakes.PancakeRecipe;

public class PancakeManagerImpl implements PancakeManager {
	// business constraints
	public static final int MAX_PANCAKE_COUNT = 100;
	public static final int MAX_PANCAKE_PER_ORDER = 500;
	
	private final PancakeBuilderFactory pancakeBuilderFactory;

    public PancakeManagerImpl(PancakeBuilderFactory pancakeBuilderFactory) {
        this.pancakeBuilderFactory = pancakeBuilderFactory;
    }

	/* Assumption:
	 * Design pattern should be used to avoid hardcoding recipes for pancakes and to allow disciples to choose the ingredients ??
	 */
	@Override
    public void addPancakes(OrderInterface order, List<String> ingredients, int count) throws Exception {
        count = Math.min(count, MAX_PANCAKE_COUNT);
        for (int i = 0; i < count && order.getPancakes().size() < MAX_PANCAKE_PER_ORDER; i++) {
        	order.addPancake(createPancake(ingredients));
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
	
	private PancakeRecipe createPancake(List<String> ingredients) throws Exception {
		PancakeBuilder builder = pancakeBuilderFactory.createBuilder();
		for (String ingredient : ingredients) {
			builder.addIngredient(ingredient);
		}
		return builder.build();
	}
}
