package ru.pashkovske.buratino.tinkoff.service.order.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PriceType;
import ru.tinkoff.piapi.core.OrdersService;

import ru.pashkovske.buratino.tinkoff.service.order.mapper.OrderDataMapper;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderRequest;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderResponse;
import ru.pashkovske.buratino.tinkoff.service.price.PriceUtils;

@RequiredArgsConstructor
public class OrderTinkoffOfficialApi implements OrderApi {
    Logger logger = LoggerFactory.getLogger(OrderTinkoffOfficialApi.class);
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
        logger.info(String.format(
                "Posted order:\n\tuid=%s;\n\tprice_units=%d;\n\tprice_nanos=%d;",
                response.getInstrumentUid(),
                response.getInitialSecurityPrice().getUnits(),
                response.getInitialSecurityPrice().getNano()
        ));
        return OrderDataMapper.map(response);
    }

    @Override
    @NonNull
    public OrderResponse replaceOrder(@NonNull String orderId, @NonNull OrderRequest order) {
        PostOrderResponse response = tinkoffOrderService.replaceOrderSync(
                brokerAccountId,
                order.lotsQuantity(),
                PriceUtils.map(order.price()),
                UUID.randomUUID().toString(),
                orderId,
                PriceType.PRICE_TYPE_CURRENCY
        );
        logger.info(String.format(
                "Replaced order:\n\tuid=%s;\n\tprice_units=%d;\n\tprice_nanos=%d;",
                response.getInstrumentUid(),
                response.getInitialSecurityPrice().getUnits(),
                response.getInitialSecurityPrice().getNano()
        ));
        return OrderDataMapper.map(response);
    }

    @Override
    public void cancelOrder(@NonNull String orderId) {
        tinkoffOrderService.cancelOrderSync(brokerAccountId, orderId);
        logger.info(String.format(
                "Canceled order:\n\tuid=%s;",
                orderId
        ));
    }

    @Override
    public @NonNull OrderResponse getOrder(@NonNull String orderId) {
        return OrderDataMapper.map(tinkoffOrderService.getOrderStateSync(brokerAccountId, orderId));
    }
}
