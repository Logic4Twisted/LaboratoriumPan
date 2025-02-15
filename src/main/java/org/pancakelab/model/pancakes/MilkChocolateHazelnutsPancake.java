package org.pancakelab.model.pancakes;

import java.util.List;

public class MilkChocolateHazelnutsPancake extends MilkChocolatePancake {

    @Override
    public List<String> ingredients() {
        return List.of("milk chocolate", "hazelnuts");
    }
}
