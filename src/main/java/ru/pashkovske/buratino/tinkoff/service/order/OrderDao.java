package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.order.OrderDto;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

import java.util.List;

public interface OrderDao {
    boolean post(OrderDto orderDto);
    boolean update(OrderDto orderDto);
    OrderDto get(String id);
    List<OrderDto> findByInstrument(String instrumentId);
    List<OrderDto> findByInstrumentAndDirection(String instrumentId, OrderDirection direction);
    boolean delete(OrderDto orderDto);
}
