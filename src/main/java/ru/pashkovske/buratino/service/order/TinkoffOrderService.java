package ru.pashkovske.buratino.service.order;

import lombok.AllArgsConstructor;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OrdersService;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class TinkoffOrderService implements OrderService {
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
    public void sellBestByOrderBook(Share share) {
        Quotation bestSellPrice = marketDataService.getOrderBookSync(share.getFigi(), 1).getAsks(0).getPrice();
        Quotation price = Quotation.newBuilder()
                .setUnits(bestSellPrice.getUnits() - share.getMinPriceIncrement().getUnits())
                .setNano(bestSellPrice.getNano() - share.getMinPriceIncrement().getNano())
                .build();
        processOrder(share.getFigi(), 1, price, OrderDirection.ORDER_DIRECTION_SELL);
    }

    @Override
    public void buyBestByOrderBook(Share share) {
        Quotation bestSellPrice = marketDataService.getOrderBookSync(share.getFigi(), 1).getBids(0).getPrice();
        Quotation price = Quotation.newBuilder()
                .setUnits(bestSellPrice.getUnits() + share.getMinPriceIncrement().getUnits())
                .setNano(bestSellPrice.getNano() + share.getMinPriceIncrement().getNano())
                .build();
        processOrder(share.getFigi(), 1, price, OrderDirection.ORDER_DIRECTION_BUY);
    }
}
