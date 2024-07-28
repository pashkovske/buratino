package ru.pashkovske.buratino.tinkoff.service.order;

import lombok.AllArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.OrdersService;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class OrderApi implements OrderServant {
    final String brokerAccountId;
    final OrdersService tinkoffOrderService;

    public void postOrder(
            @Nonnull InstrumentHolder instrument,
            int lotQuantity,
            @Nonnull Quotation price,
            @Nonnull OrderDirection direction,
            @Nonnull OrderType type) {
        tinkoffOrderService.postOrderSync(
                instrument.getFigi(),
                lotQuantity,
                price,
                direction,
                brokerAccountId,
                type,
                UUID.randomUUID().toString()
        );
    }

    @Override
    public void replaceOrder(@Nonnull String orderId, int lotQuantity, @Nonnull Quotation price) {

    }

    @Override
    public void CancelOrder(@Nonnull String orderId) {

    }
}
