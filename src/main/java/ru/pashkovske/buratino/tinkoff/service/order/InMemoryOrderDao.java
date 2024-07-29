package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.order.OrderDto;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryOrderDao implements OrderDao {

    private final Map<String, OrderDto> orders = new HashMap<>();

    @Override
    public boolean post(OrderDto orderDto) {
        if (orders.containsKey(orderDto.getId())) {
            throw new IllegalArgumentException("Попытка загрузить заявку с тем же id");
        }
        orders.put(orderDto.getId(), orderDto);
        return true;
    }

    @Override
    public boolean update(OrderDto orderDto) {
        if (orders.containsKey(orderDto.getId())) {
            orders.replace(orderDto.getId(), orderDto);
            return true;
        }
        throw new NullPointerException("Попытка обновить несуществующую заявку");
    }

    @Override
    public OrderDto get(String id) {
        return orders.get(id);
    }

    @Override
    public List<OrderDto> findByInstrument(String instrumentId) {
        return orders.values().stream()
                .filter(order -> order.getInstrumentId().equals(instrumentId))
                .toList();
    }

    @Override
    public List<OrderDto> findByInstrumentAndDirection(String instrumentId, OrderDirection direction) {
        return orders.values().stream()
                .filter(order -> order.getInstrumentId().equals(instrumentId) && order.getDirection().equals(direction))
                .toList();
    }

    @Override
    public boolean delete(OrderDto orderDto) {
        if (orders.containsKey(orderDto.getId())) {
            orders.remove(orderDto.getId());
            return true;
        }
        throw new NullPointerException("Попытка удалить несуществующую заявку");
    }
}
