package ru.pashkovske.buratino.tinkoff.service.order.strategy.job;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.mapper.OrderDataMapper;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderHolder;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderRequest;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.price.mapper.PriceMapper;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.TimeInForceType;

import java.util.List;

@RequiredArgsConstructor
public class FollowBestPriceRefresher implements Runnable {
    private final OrderApi orderApi;
    private final MarketPriceService marketPriceService;
    private final InstrumentSelector instrumentSelector;
    private final Assignment assignment;

    @Override
    public void run() {
        AssignmentCommand command = assignment.getCommand();

        MoneyValue price = PriceMapper.map(
                marketPriceService.getBestPrice(
                        command.getInstrument(),
                        assignment.getOrders().stream().map(OrderDataMapper::map).toList(),
                        command.getDirection()
                ),
                command.getInstrument()
        );

        List<OrderHolder> orders = assignment.getOrders();
        assert orders.size() == 1;
        OrderHolder order = orders.getFirst();
        if (!order.getOrderRequest().price().equals(price)) {
            if (orderApi.getOrder(order.getOrderResponse().id()).isActive()) {
                String orderId = order.getOrderResponse().id();

                OrderRequest orderRequest = new OrderRequest(
                        command.getInstrument().getId().id(),
                        price,
                        command.getDirection(),
                        command.getLotsQuantity(),
                        OrderType.ORDER_TYPE_LIMIT,
                        TimeInForceType.TIME_IN_FORCE_DAY
                );

                order.setOrderRequest(orderRequest);
                order.setOrderResponse(orderApi.replaceOrder(orderId, orderRequest));
            }
        }
    }
}
