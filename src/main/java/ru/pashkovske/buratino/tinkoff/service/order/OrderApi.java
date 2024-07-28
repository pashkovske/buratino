package ru.pashkovske.buratino.tinkoff.service.order;

import lombok.AllArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.OrdersService;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class OrderApi<T> implements OrderServant<T> {
    final String brokerAccountId;
    final OrdersService tinkoffOrderService;

    public String postOrder(
            @Nonnull InstrumentHolder<T> instrument,
            long lotQuantity,
            @Nonnull Quotation price,
            @Nonnull OrderDirection direction,
            @Nonnull OrderType type) {
        PostOrderResponse response = tinkoffOrderService.postOrderSync(
                instrument.getFigi(),
                lotQuantity,
                price,
                direction,
                brokerAccountId,
                type,
                UUID.randomUUID().toString()
        );
        return response.getOrderId();
    }

    @Override
    public void replaceOrder(
            @Nonnull String orderId,
            long lotQuantity,
            @Nonnull Quotation price) {
        tinkoffOrderService.replaceOrder(
                brokerAccountId,
                lotQuantity,
                price,
                UUID.randomUUID().toString(),
                orderId,
                PriceType.PRICE_TYPE_UNSPECIFIED
        );
    }

    @Override
    public void CancelOrder(@Nonnull String orderId) {

    }
}
