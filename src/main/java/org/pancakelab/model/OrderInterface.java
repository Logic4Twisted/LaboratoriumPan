package org.pancakelab.model;

import org.pancakelab.model.pancakes.OrderRepository;
import java.util.List;
import java.util.UUID;

public interface OrderInterface {

    UUID getId();

    int getBuilding();

    int getRoom();

    void addPancake(List<String> ingredients) throws Exception;

    boolean removePancake(String description) throws Exception;

    List<String> getPancakes();

    List<String> getPancakesToDeliver();

    void complete() throws Exception;

    void prepare() throws Exception;

    void deliver() throws Exception;

    void cancel() throws Exception;

    void updateRepository(OrderRepository orderRepository);

    boolean isInitated();

    boolean isCompleted();

    boolean isPrepared();

    boolean isDelivered();
}
