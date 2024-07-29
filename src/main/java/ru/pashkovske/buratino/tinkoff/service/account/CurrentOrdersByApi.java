package ru.pashkovske.buratino.tinkoff.service.account;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.mapper.OrderDataMapper;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderDto;
import ru.pashkovske.buratino.tinkoff.service.order.OrderDao;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.core.OrdersService;

import java.util.List;

@RequiredArgsConstructor
public class CurrentOrdersByApi<T> implements CurrentAccountOrders<T> {
    private final OrdersService ordersService;
    private final String accountId;

    @Override
    public List<OrderState> getOrdersByInstrument(InstrumentHolder<T> instrument) {
        return ordersService.getOrdersSync(accountId).stream()
                .filter(order -> order.getFigi().equals(instrument.getFigi()))
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersDataByInstrument(InstrumentHolder<T> instrument) {
        return getOrdersByInstrument(instrument).stream()
                .map(OrderDataMapper::map)
                .toList();
    }

    @Override
    public OrderDao synchronizedOrderDaoFactory(OrderDao orderDao) {
        ordersService.getOrdersSync(accountId).stream()
                .map(OrderDataMapper::map)
                .forEach(orderDao::post);
        return orderDao;
    }
}
