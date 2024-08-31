package ru.pashkovske.buratino.tinkoff.service.assignment;

import lombok.Data;
import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderHolder;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Data
public class FollowPriceAssignment implements Assignment {
    @NonNull UUID id;
    @NonNull OrderHolder order;
    @NonNull AssignmentCommand command;
    ScheduledFuture<?> scheduledFuture;

    @Override
    public List<OrderHolder> getOrders() {
        return List.of(order);
    }
}
