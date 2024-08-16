package ru.pashkovske.buratino.tinkoff.service.assignment.model;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderDto;

import javax.annotation.Nonnull;
import java.util.UUID;

public record AssignmentStateDto(
        @NonNull UUID id,
        @Nonnull OrderDto currentOrder,
        @Nonnull String instrumentTicker,
        @NonNull String instrumentName
) {
}
