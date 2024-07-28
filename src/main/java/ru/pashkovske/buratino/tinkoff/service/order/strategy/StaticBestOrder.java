package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.price.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderCommand;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderData;
import ru.pashkovske.buratino.tinkoff.service.order.OrderServant;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
public class StaticBestOrder<T> implements OrderStrategy<T> {
    private final OrderServant<T> orderServant;
    private final CurrentMarketPriceService<T> marketPriceService;

    private Map<String, OrderData> myOrders = new HashMap<>();

    @Override
    public void postOrder(OrderCommand<T> command) {
        InstrumentHolder<T> instrument = command.getInstrument();
        Quotation price = marketPriceService.getBestPrice(command.getInstrument(), command.getDirection());
        long lots = command.getLotQuantity();
        OrderDirection direction = command.getDirection();
        OrderType type = OrderType.ORDER_TYPE_LIMIT;

        String orderId = orderServant.postOrder(
                instrument,
                lots,
                price,
                direction,
                type);

        OrderData orderData = OrderData.builder()
                .id(orderId)
                .instrumentId(instrument.getFigi())
                .lotsQuantity(lots)
                .price(price)
                .direction(direction)
                .type(type)
                .build();
        myOrders.put(instrument.getFigi(), orderData);
    }

    @Override
    public void putOrder(OrderCommand<T> command) {

        InstrumentHolder<T> instrument = command.getInstrument();
        OrderData orderMade = myOrders.get(instrument.getFigi());
        List<Order> ordersMade = new ArrayList<>();
        if (orderMade != null) {
            ordersMade.add(Order.newBuilder()
                            .setPrice(orderMade.getPrice())
                            .setQuantity(orderMade.getLotsQuantity())
                    .build());
        }
        OrderDirection direction = orderMade.getDirection();
        Quotation price = marketPriceService.getBestPrice(command.getInstrument(), ordersMade, direction);
        long lots = command.getLotQuantity();
        OrderType type = OrderType.ORDER_TYPE_LIMIT;

        if (! price.equals(orderMade.getPrice())) {
            orderServant.replaceOrder(
                    orderMade.getId(),
                    lots,
                    price);

            OrderData orderData = OrderData.builder()
                    .id(orderMade.getId())
                    .instrumentId(instrument.getFigi())
                    .lotsQuantity(lots)
                    .price(price)
                    .direction(direction)
                    .type(type)
                    .build();
            myOrders.put(instrument.getFigi(), orderData);
        }
    }

    @Override
    public void deleteOrder(InstrumentHolder<T> instrument) {

    }
}
