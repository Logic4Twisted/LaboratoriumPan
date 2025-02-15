package org.pancakelab.model.pancakes;

import java.util.List;

public class MilkChocolatePancake implements PancakeType {

    @Override
    public List<String> ingredients() {
        return List.of("milk chocolate");
    }
}
