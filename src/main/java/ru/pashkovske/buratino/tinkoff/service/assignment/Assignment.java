package ru.pashkovske.buratino.tinkoff.service.assignment;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderHolder;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public interface Assignment {
    @NonNull UUID getId();
    List<OrderHolder> getOrders();
    @NonNull AssignmentCommand getCommand();
    ScheduledFuture<?> getScheduledFuture();
    void setScheduledFuture(ScheduledFuture<?> scheduledFuture);
}
