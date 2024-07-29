package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentOrdersByApi;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.price.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.selector.FutureSelector;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.selector.ShareSelector;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.SimpleBuyOneCommand;
import ru.pashkovske.buratino.tinkoff.service.model.order.SimpleSellOneCommand;
import ru.pashkovske.buratino.tinkoff.service.order.InMemoryOrderDao;
import ru.pashkovske.buratino.tinkoff.service.order.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.OrderDao;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.OrderStrategy;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.StaticBestOrder;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        String accountId = new AccountResolverImpl("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();

        CurrentAccountOrders currentAccountOrders = new CurrentOrdersByApi(investApi.getOrdersService(), accountId);

        OrderDao currentOrders = new InMemoryOrderDao();
        currentOrders = currentAccountOrders.synchronizedOrderDaoFactory(currentOrders);
        OrderStrategy orderMaker = new StaticBestOrder(
                new OrderApi(
                        accountId,
                        investApi.getOrdersService()),
                new CurrentMarketPriceService(investApi.getMarketDataService()),
                currentOrders
                );

        String ticker = "HOH5";
        InstrumentHolder<Future> inst1 = new FutureSelector(investApi.getInstrumentsService()).getByTicker(ticker);
        SimpleBuyOneCommand command1 = new SimpleBuyOneCommand(inst1);
        ticker = "KMM5";
        InstrumentHolder<Future> inst2 = new FutureSelector(investApi.getInstrumentsService()).getByTicker(ticker);
        SimpleBuyOneCommand command2 = new SimpleBuyOneCommand(inst2);
        //orderMaker.postOrder(command);
        while (true) {
            orderMaker.putOrder(command1);
            orderMaker.putOrder(command2);
            Thread.sleep(40000);
        }
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}