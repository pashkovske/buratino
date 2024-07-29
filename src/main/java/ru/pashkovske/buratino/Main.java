package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentOrdersByApi;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.price.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.selector.FutureSelector;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.selector.ShareSelector;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderData;
import ru.pashkovske.buratino.tinkoff.service.model.order.SimpleBuyOneCommand;
import ru.pashkovske.buratino.tinkoff.service.model.order.SimpleSellOneCommand;
import ru.pashkovske.buratino.tinkoff.service.order.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.OrderStrategy;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.StaticBestOrder;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        String ticker = "HOH5";
        InstrumentHolder<Future> share = new FutureSelector(investApi.getInstrumentsService()).getByTicker(ticker);
        String accountId = new AccountResolverImpl("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();

        CurrentAccountOrders currentAccountOrders = new CurrentOrdersByApi(investApi.getOrdersService(), accountId);
        List<OrderData> activeOrders = currentAccountOrders.getOrdersDataByInstrument(share);
        Map<String, OrderData> orders = new HashMap<>();
        if (! activeOrders.isEmpty()) {
            orders.put(share.getFigi(), activeOrders.getFirst());
        }

        OrderStrategy orderMaker = new StaticBestOrder(
                new OrderApi(
                        accountId,
                        investApi.getOrdersService()),
                new CurrentMarketPriceService(investApi.getMarketDataService()),
                orders
                );


        //orderMaker.putOrder(new SimpleBuyOneCommand(share));
        //orderMaker.putOrder(new SimpleSellOneCommand(share));
        while (true) {
            orderMaker.putOrder(new SimpleBuyOneCommand(share));
            //orderMaker.putOrder(new SimpleSellOneCommand(share));
            Thread.sleep(40000);
        }
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}