package org.pancakelab.model.pancakes;

import java.util.List;

public class DarkChocolateWhippedCreamHazelnutsPancake extends DarkChocolateWhippedCreamPancake {

    @Override
    public List<String> ingredients() {
        return List.of("dark chocolate", "mustard", "whipped cream", "hazelnuts");
    }
}
