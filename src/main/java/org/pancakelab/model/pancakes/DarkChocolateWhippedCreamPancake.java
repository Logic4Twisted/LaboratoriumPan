package org.pancakelab.model.pancakes;

import java.util.List;

public class DarkChocolateWhippedCreamPancake extends DarkChocolatePancake {

    @Override
    public List<String> ingredients() {
        return List.of("dark chocolate", "whipped cream");
    }
}
