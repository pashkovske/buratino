package ru.pashkovske.buratino.tinkoff.service.market.instrument.price;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.utils.PriceUtils;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.MarketDataService;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class CurrentMarketPriceService<T> {
    private final MarketDataService marketDataService;

    public Quotation getBestSellPrice(InstrumentHolder<T> instrument, List<Order> excludeOrders) {
        List<Order> asks = marketDataService.getOrderBookSync(instrument.getFigi(), excludeOrders.size() + 1).getAsksList();
        Order minAsk = subtractOrderBooks(asks, excludeOrders).getFirst();
        if (minAsk.getQuantity() <= 0) {
            throw new IllegalStateException("Из стакана исключён несуществующий заказ");
        }
        return PriceUtils.minus(minAsk.getPrice(), instrument.getMinPriceIncrement());
    }

    public Quotation getBestSellPrice(InstrumentHolder<T> instrument) {
        return this.getBestSellPrice(instrument, List.of());
    }

    public Quotation getBestBuyPrice(InstrumentHolder<T> instrument, List<Order> excludeOrders) {
        List<Order> bids = marketDataService.getOrderBookSync(instrument.getFigi(), excludeOrders.size() + 1).getBidsList();
        Order minBid = subtractOrderBooks(bids, excludeOrders).reversed().getFirst();
        if (minBid.getQuantity() <= 0) {
            throw new IllegalStateException("Из стакана исключён несуществующий заказ");
        }
        return PriceUtils.plus(minBid.getPrice(), instrument.getMinPriceIncrement());
    }

    public Quotation getBestBuyPrice(InstrumentHolder<T> instrument) {
        return this.getBestBuyPrice(instrument, List.of());
    }

    public Quotation getBestPrice(InstrumentHolder<T> instrument, List<Order> excludeOrders, OrderDirection direction) {
        if (direction == OrderDirection.ORDER_DIRECTION_BUY) {
            return getBestBuyPrice(instrument, excludeOrders);
        } else if (direction == OrderDirection.ORDER_DIRECTION_SELL) {
            return getBestSellPrice(instrument, excludeOrders);
        }
        else {
            throw new IllegalStateException("Определение лучшей цены не зависимо от направления сделки не реализовано");
        }
    }

    public Quotation getBestPrice(InstrumentHolder<T> instrument, OrderDirection direction) {
        return this.getBestPrice(instrument, List.of(), direction);
    }

    private List<Order> subtractOrderBooks(@Nonnull List<Order> minuend, @Nonnull List<Order> subtrahend) {
        HashMap<Quotation, Long> resultMap = new HashMap<>();
        for (Order order : minuend) {
            Long orderLots = order.getQuantity();
            resultMap.computeIfPresent(order.getPrice(), (price, lots) -> lots + orderLots);
            resultMap.putIfAbsent(order.getPrice(), orderLots);
        }
        for (Order order : subtrahend) {
            Long orderLots = order.getQuantity();
            resultMap.computeIfPresent(order.getPrice(), (price, lots) -> lots - orderLots);
            resultMap.putIfAbsent(order.getPrice(), -orderLots);
        }
        return resultMap.keySet().stream()
                .sorted(PriceUtils.getPriceComparator())
                .map(key -> Order.newBuilder()
                        .setPrice(key)
                        .setQuantity(resultMap.get(key))
                        .build())
                .filter(order -> order.getQuantity() != 0)
                .toList();
    }
}
