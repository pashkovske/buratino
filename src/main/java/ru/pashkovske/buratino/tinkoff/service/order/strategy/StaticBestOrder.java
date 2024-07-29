package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.price.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderCommand;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderDto;
import ru.pashkovske.buratino.tinkoff.service.order.OrderDao;
import ru.pashkovske.buratino.tinkoff.service.order.OrderServant;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class StaticBestOrder<T> implements OrderStrategy<T> {
    private final OrderServant<T> orderServant;
    private final CurrentMarketPriceService<T> marketPriceService;
    private final OrderDao currentOrders;

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

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .instrumentId(instrument.getFigi())
                .lotsQuantity(lots)
                .price(price)
                .direction(direction)
                .type(type)
                .updateTime(Instant.now())
                .build();
        currentOrders.post(orderDto);
    }

    @Override
    public void putOrder(OrderCommand<T> command) {

        InstrumentHolder<T> instrument = command.getInstrument();
        List<OrderDto> ordersData = currentOrders.findByInstrumentAndDirection(instrument.getFigi(), command.getDirection());
        assert ordersData.size() == 1;
        OrderDto orderDto = ordersData.getFirst();
        assert orderDto.getDirection().equals(command.getDirection());
        List<Order> ordersMade = List.of(Order.newBuilder()
                .setPrice(orderDto.getPrice())
                .setQuantity(orderDto.getLotsQuantity())
                .build());
        OrderDirection direction = orderDto.getDirection();
        Quotation price = marketPriceService.getBestPrice(command.getInstrument(), ordersMade, direction);
        long lots = command.getLotQuantity();
        OrderType type = OrderType.ORDER_TYPE_LIMIT;

        if (! price.equals(orderDto.getPrice())) {
            orderServant.replaceOrder(
                    orderDto.getId(),
                    lots,
                    price);

            OrderDto newOrderDto = OrderDto.builder()
                    .id(orderDto.getId())
                    .instrumentId(instrument.getFigi())
                    .lotsQuantity(lots)
                    .price(price)
                    .direction(direction)
                    .type(type)
                    .updateTime(Instant.now())
                    .build();
            currentOrders.update(newOrderDto);
        }
    }

    @Override
    public void deleteOrder(InstrumentHolder<T> instrument) {

    }
}
