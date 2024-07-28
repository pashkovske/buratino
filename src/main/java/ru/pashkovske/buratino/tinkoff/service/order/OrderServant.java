package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;

import javax.annotation.Nonnull;

public interface OrderServant<T> {
    String postOrder(
            @Nonnull InstrumentHolder<T> instrument,
            long lotQuantity,
            @Nonnull Quotation price,
            @Nonnull OrderDirection direction,
            @Nonnull OrderType type);
    void replaceOrder(
            @Nonnull String orderId,
            long lotQuantity,
            @Nonnull Quotation price);
    void CancelOrder(@Nonnull String orderId);
}
