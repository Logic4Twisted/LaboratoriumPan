package org.pancakelab.model;

import org.pancakelab.model.pancakes.OrderRepository;
import java.util.List;
import java.util.UUID;

public interface OrderInterface {

    UUID getId();

    int getBuilding();

    int getRoom();

    void addPancake(List<String> ingredients);

    boolean removePancake(String description);

    List<String> getPancakes();

    List<String> getPancakesToDeliver();

    void completed(OrderRepository orderRepository);

    void prepared(OrderRepository orderRepository);

    void delivered(OrderRepository orderRepository);

    void cancel(OrderRepository orderRepository);

    void saveTo(OrderRepository orderRepository);

    boolean isInitated();

    boolean isCompleted();

    boolean isPrepared();

    boolean isDelivered();
}
