package org.pancakelab.model.pancakes;

public class PancakeBuilderFactoryImpl implements PancakeBuilderFactory {
    @Override
    public PancakeBuilder createBuilder() {
        return new PancakeBuilderImpl();
    }
}
