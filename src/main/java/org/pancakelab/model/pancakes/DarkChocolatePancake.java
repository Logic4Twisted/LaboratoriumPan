package org.pancakelab.model.pancakes;

import java.util.List;

public class DarkChocolatePancake implements PancakeType {

    @Override
    public List<String> ingredients() {
        return List.of("dark chocolate");
    }
}
