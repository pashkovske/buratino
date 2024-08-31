package ru.pashkovske.buratino.tinkoff.service.order.model;

import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus;
import ru.tinkoff.piapi.contract.v1.OrderType;

import javax.annotation.Nonnull;
import java.time.Instant;

public record OrderResponse(
        @Nonnull String id,
        @Nonnull String instrumentId,
        @Nonnull MoneyValue price,
        @Nonnull MoneyValue commission,
        @Nonnull Instant time,
        @Nonnull OrderDirection direction,
        long lotsQuantity,
        @Nonnull OrderType type,
        @Nonnull String UUID,
        @Nonnull OrderExecutionReportStatus status
) {
    public boolean isActive() {
        return status.equals(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_NEW) ||
                status.equals(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_PARTIALLYFILL);
    }
}
