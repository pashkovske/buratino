package ru.pashkovske.buratino.tinkoff.service.order;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.core.OrdersService;

import java.util.List;

@RequiredArgsConstructor
public class CurrentOrderByApi implements CurrentOrderFacade {
    private final OrdersService ordersService;
    private final String accountId;

    @Override
    public List<OrderState> getOrdersByInstrument(InstrumentHolder instrument) {
        return ordersService.getOrdersSync(accountId).stream()
                .filter(order -> order.getFigi().equals(instrument.getFigi()))
                .toList();
    }
}
