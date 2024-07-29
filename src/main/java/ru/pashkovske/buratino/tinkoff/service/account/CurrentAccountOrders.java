package ru.pashkovske.buratino.tinkoff.service.account;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderDto;
import ru.pashkovske.buratino.tinkoff.service.order.OrderDao;
import ru.tinkoff.piapi.contract.v1.OrderState;

import java.util.List;

public interface CurrentAccountOrders<T> {
    List<OrderState> getOrdersByInstrument(InstrumentHolder<T> instrument);
    List<OrderDto> getOrdersDataByInstrument(InstrumentHolder<T> instrument);
    OrderDao synchronizedOrderDaoFactory(OrderDao orderDao);
}
