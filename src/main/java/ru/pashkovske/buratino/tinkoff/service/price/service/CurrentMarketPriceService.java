package ru.pashkovske.buratino.tinkoff.service.price.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.price.PriceUtils;
import ru.pashkovske.buratino.tinkoff.service.price.mapper.PriceMapper;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.MarketDataService;

import java.util.List;


@RequiredArgsConstructor
public class CurrentMarketPriceService implements MarketPriceService {
    private final MarketDataService marketDataService;
    private final OrderBook bidsOrderBook = new OrderBookIml();
    private final OrderBook asksOrderBook = new OrderBookIml();
    private int requiredDepth;

    @Override
    public long getSpreadBasisPoints(@NonNull InstrumentWrapper instrument) {
        setRequiredDepth(List.of());
        putRawOrderBooks(instrument);
        Quotation step = instrument.getMinPriceIncrement();
        Quotation bestSellQuotation = getBestSellPrice(instrument);
        Quotation bestBuyQuotation = getBestBuyPrice(instrument);
        if (bestBuyQuotation == null || bestSellQuotation == null) {
            return -1;
        }
        long bestSellPrice = PriceUtils.priceInSteps(bestSellQuotation, step);
        long bestBuyPrice = PriceUtils.priceInSteps(bestBuyQuotation, step);
        return ((bestSellPrice - bestBuyPrice) *20000) / (bestSellPrice + bestBuyPrice);
    }

    public Quotation getBestPrice(
            @NonNull InstrumentWrapper instrument,
            List<Order> excludeOrders,
            @NonNull OrderDirection direction) {
        setRequiredDepth(excludeOrders);
        putRawOrderBooks(instrument);
        List<Order> excludesInPts = excludeOrders.stream()
                .map(order -> Order.newBuilder()
                        .setPrice(PriceMapper.mapMoneyToPtsQuotation(order.getPrice(), instrument))
                        .setQuantity(order.getQuantity())
                        .build()
                )
                .toList();
        if (direction == OrderDirection.ORDER_DIRECTION_BUY) {
            return getBestBuyPrice(instrument, excludesInPts);
        } else if (direction == OrderDirection.ORDER_DIRECTION_SELL) {
            return getBestSellPrice(instrument, excludesInPts);
        }
        else {
            throw new IllegalStateException("Определение лучшей цены не зависимо от направления сделки не реализовано");
        }
    }

    public Quotation getBestPrice(@NonNull InstrumentWrapper instrument, @NonNull OrderDirection direction) {
        return this.getBestPrice(instrument, List.of(), direction);
    }

    private Quotation getBestSellPrice(InstrumentWrapper instrument, List<Order> excludeOrders) {
        if (asksOrderBook.getSize() < requiredDepth) {
            return null;
        }
        excludeAsks(excludeOrders);
        return PriceUtils.minus(asksOrderBook.getMinPrice(), instrument.getMinPriceIncrement());
    }

    private Quotation getBestSellPrice(InstrumentWrapper instrument) {
        return this.getBestSellPrice(instrument, List.of());
    }

    private Quotation getBestBuyPrice(InstrumentWrapper instrument, List<Order> excludeOrders) {
        if (bidsOrderBook.getSize() < requiredDepth) {
            return null;
        }
        excludeBids(excludeOrders);
        return PriceUtils.plus(bidsOrderBook.getMaxPrice(), instrument.getMinPriceIncrement());
    }

    private Quotation getBestBuyPrice(InstrumentWrapper instrument) {
        return this.getBestBuyPrice(instrument, List.of());
    }

    private void putRawOrderBooks(InstrumentWrapper instrument) {
        GetOrderBookResponse orderBookResponse = marketDataService.getOrderBookSync(instrument.getId().id(), requiredDepth);
        asksOrderBook.clear();
        asksOrderBook.putAll(orderBookResponse.getAsksList());
        bidsOrderBook.clear();
        bidsOrderBook.putAll(orderBookResponse.getBidsList());
    }

    private void excludeBids(List<Order> excludeOrders) {
        bidsOrderBook.excludeAll(excludeOrders);
    }

    private void excludeAsks(List<Order> excludeOrders) {
        asksOrderBook.excludeAll(excludeOrders);
    }

    private void setRequiredDepth(List<Order> excludeOrders) {
        requiredDepth = excludeOrders.size() + 1;
    }
}
