package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import ru.pashkovske.buratino.tinkoff.service.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.tinkoff.piapi.contract.v1.OrderState;

import java.util.List;
import java.util.UUID;

public interface OrderStrategy {
    Assignment post(AssignmentCommand command);
    List<Assignment> pull(List<OrderState> orders);
    List<Assignment> pullAndSchedule(List<OrderState> orders);
    void scheduleRefresh(UUID assignmentId);
    Assignment refresh(UUID assignmentId) throws InterruptedException;
    List<Assignment> refreshAll() throws InterruptedException;
    void cancel(UUID assignmentId);
    List<Assignment> getAll();
}
