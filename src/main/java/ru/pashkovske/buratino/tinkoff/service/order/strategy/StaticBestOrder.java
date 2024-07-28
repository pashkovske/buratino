package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.price.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderCommand;
import ru.pashkovske.buratino.tinkoff.service.order.OrderServant;
import ru.tinkoff.piapi.contract.v1.OrderType;

@RequiredArgsConstructor
public class StaticBestOrder<T> implements OrderStrategy<T> {
    private final OrderServant<T> orderServant;
    private final CurrentMarketPriceService<T> marketPriceService;

    @Override
    public void postOrder(OrderCommand<T> command) {
            orderServant.postOrder(
                    command.getInstrument(),
                    command.getLotQuantity(),
                    marketPriceService.getBestPrice(command.getInstrument(), command.getDirection()),
                    command.getDirection(),
                    OrderType.ORDER_TYPE_LIMIT);
    }

    @Override
    public void putOrder(OrderCommand<T> command) {

    }

    @Override
    public void deleteOrder(InstrumentHolder<T> instrument) {

    }
}
