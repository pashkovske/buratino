package ru.pashkovske.buratino.tinkoff.service.order.mapper;

import com.google.protobuf.Timestamp;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderDto;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderHolder;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderResponse;
import ru.pashkovske.buratino.tinkoff.service.price.PriceUtils;
import ru.pashkovske.buratino.tinkoff.service.price.mapper.PriceMapper;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;

import java.time.Instant;

public class OrderDataMapper {
    public static Order map(OrderHolder orderHolder) {
        OrderResponse orderResponse = orderHolder.getOrderResponse();
        return Order.newBuilder()
                .setPrice(PriceUtils.map(orderResponse.price()))
                .setQuantity(orderResponse.lotsQuantity())
                .build();
    }
    public static OrderResponse map(PostOrderResponse postOrderResponse) {
        Timestamp ts = postOrderResponse.getResponseMetadata().getServerTime();
        return new OrderResponse(
                postOrderResponse.getOrderId(),
                postOrderResponse.getInstrumentUid(),
                postOrderResponse.getInitialSecurityPrice(),
                postOrderResponse.getExecutedCommission(),
                Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()),
                postOrderResponse.getDirection(),
                postOrderResponse.getLotsRequested(),
                postOrderResponse.getOrderType(),
                postOrderResponse.getOrderRequestId(),
                postOrderResponse.getExecutionReportStatus()
        );
    }
    public static OrderResponse map(OrderState orderState) {
        Timestamp ts = orderState.getOrderDate();
        return new OrderResponse(
                orderState.getOrderId(),
                orderState.getInstrumentUid(),
                orderState.getInitialSecurityPrice(),
                orderState.getExecutedCommission(),
                Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()),
                orderState.getDirection(),
                orderState.getLotsRequested(),
                orderState.getOrderType(),
                orderState.getOrderRequestId(),
                orderState.getExecutionReportStatus()
        );
    }

    public static OrderDto mapResponse(OrderHolder orderHolder) {
        OrderResponse response = orderHolder.getOrderResponse();
        return new OrderDto(
                response.id(),
                PriceMapper.map(response.price()),
                response.lotsQuantity(),
                response.direction()
        );
    }
}
