package ru.pashkovske.buratino.tinkoff.service.account;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderData;
import ru.pashkovske.buratino.tinkoff.service.utils.PriceUtils;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.Quotation;
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

    public List<OrderData> getOrdersDataByInstrument(InstrumentHolder<T> instrument) {
        return getOrdersByInstrument(instrument).stream()
                .map(orderState -> {
                    Quotation price = Quotation.newBuilder()
                            .setUnits(orderState.getInitialSecurityPrice().getUnits())
                            .setNano(orderState.getInitialSecurityPrice().getNano())
                            .build();
                    return OrderData.builder()
                            .id(orderState.getOrderId())
                            .instrumentId(orderState.getFigi())
                            .price(price)
                            .lotsQuantity(orderState.getLotsRequested())
                            .type(orderState.getOrderType())
                            .direction(orderState.getDirection())
                            .build();
                    })
                .toList();
    }
}
