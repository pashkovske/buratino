package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.tinkoff.piapi.contract.v1.OrderState;

import java.util.List;
import java.util.UUID;

public interface OrderStrategy {
    Assignment post(AssignmentCommand command);
    List<Assignment> pull(List<OrderState> orders);
    Assignment refresh(UUID assignmentId);
    List<Assignment> refreshAll();
    void cancel(UUID assignmentId);
}
