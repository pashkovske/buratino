package ru.pashkovske.buratino.tinkoff.service.mapper;

import ru.pashkovske.buratino.tinkoff.service.model.order.OrderDto;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.Quotation;

public class OrderDataMapper {
    public static OrderDto map(OrderState orderState) {
        Quotation price = Quotation.newBuilder()
                .setUnits(orderState.getInitialSecurityPrice().getUnits())
                .setNano(orderState.getInitialSecurityPrice().getNano())
                .build();
        return OrderDto.builder()
                .id(orderState.getOrderId())
                .instrumentId(orderState.getFigi())
                .price(price)
                .lotsQuantity(orderState.getLotsRequested())
                .type(orderState.getOrderType())
                .direction(orderState.getDirection())
                .build();
    }
}
