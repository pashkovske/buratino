package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;

import javax.annotation.Nonnull;

public interface OrderServant {
    void postOrder(
            @Nonnull InstrumentHolder instrument,
            int lotQuantity,
            @Nonnull Quotation price,
            @Nonnull OrderDirection direction,
            @Nonnull OrderType type);
    void replaceOrder(
            @Nonnull String orderId,
            int lotQuantity,
            @Nonnull Quotation price);
    void CancelOrder(@Nonnull String orderId);
}
