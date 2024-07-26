package ru.pashkovske.buratino.tinkoff.service.order;

import lombok.AllArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.utils.PriceUtils;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OrdersService;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    final String brokerAccountId;
    final MarketDataService marketDataService;
    final OrdersService tinkoffOrderService;

    void processOrder(
            @Nonnull String figi,
            int lotQuantity,
            @Nonnull Quotation price,
            @Nonnull OrderDirection orderDirection
    ) {
        tinkoffOrderService.postOrderSync(
                figi,
                lotQuantity,
                price,
                orderDirection,
                brokerAccountId,
                OrderType.ORDER_TYPE_LIMIT,
                UUID.randomUUID().toString()
        );
    }

    @Override
    public void limitSellBestByOrderBook(InstrumentHolder instrument) {
        Quotation bestSellPrice = marketDataService.getOrderBookSync(instrument.getFigi(), 1).getAsks(0).getPrice();
        Quotation price = PriceUtils.minus(bestSellPrice, instrument.getMinPriceIncrement());
        processOrder(instrument.getFigi(), 1, price, OrderDirection.ORDER_DIRECTION_SELL);
    }

    @Override
    public void limitBuyBestByOrderBook(InstrumentHolder instrument) {
        Quotation bestSellPrice = marketDataService.getOrderBookSync(instrument.getFigi(), 1).getBids(0).getPrice();
        Quotation price = PriceUtils.plus(bestSellPrice, instrument.getMinPriceIncrement());
        processOrder(instrument.getFigi(), 1, price, OrderDirection.ORDER_DIRECTION_BUY);
    }
}
