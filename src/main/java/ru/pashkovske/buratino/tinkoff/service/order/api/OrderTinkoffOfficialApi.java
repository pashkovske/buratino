package ru.pashkovske.buratino.tinkoff.service.order.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.order.mapper.OrderDataMapper;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderRequest;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderResponse;
import ru.pashkovske.buratino.tinkoff.service.price.PriceUtils;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.OrdersService;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class OrderTinkoffOfficialApi implements OrderApi {
    final String brokerAccountId;
    final OrdersService tinkoffOrderService;


    @Override
    @NonNull
    public OrderResponse post(@NonNull OrderRequest order) {
        PostOrderResponse response;
        if (order.type().equals(OrderType.ORDER_TYPE_LIMIT)) {
            response = tinkoffOrderService.postLimitOrderSync(
                    order.instrumentId(),
                    order.lotsQuantity(),
                    PriceUtils.map(order.price()),
                    order.direction(),
                    brokerAccountId,
                    order.timeInForce(),
                    PriceType.PRICE_TYPE_CURRENCY,
                    UUID.randomUUID().toString()
            );
        }
        else {
            response = tinkoffOrderService.postOrderSync(
                    order.instrumentId(),
                    order.lotsQuantity(),
                    PriceUtils.map(order.price()),
                    order.direction(),
                    brokerAccountId,
                    order.type(),
                    UUID.randomUUID().toString()
            );
        }
        return OrderDataMapper.map(response);
    }

    @Override
    @NonNull
    public OrderResponse replaceOrder(@NonNull String orderId, @NonNull OrderRequest order) {
        return OrderDataMapper.map(tinkoffOrderService.replaceOrderSync(
                brokerAccountId,
                order.lotsQuantity(),
                PriceUtils.map(order.price()),
                UUID.randomUUID().toString(),
                orderId,
                PriceType.PRICE_TYPE_CURRENCY
        ));
    }

    @Override
    public void cancelOrder(@Nonnull String orderId) {
        tinkoffOrderService.cancelOrderSync(brokerAccountId, orderId);
    }

    @Override
    public @NonNull OrderResponse getOrder(@NonNull String orderId) {
        return OrderDataMapper.map(tinkoffOrderService.getOrderStateSync(brokerAccountId, orderId));
    }
}
