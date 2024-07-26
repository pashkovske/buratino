package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.instrument.FutureSelectorImp;
import ru.pashkovske.buratino.tinkoff.service.model.FutureHolder;
import ru.pashkovske.buratino.tinkoff.service.order.OrderService;
import ru.pashkovske.buratino.tinkoff.service.order.OrderServiceImpl;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.*;

public class Main {
    public static void main(String[] args) {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        Future future = new FutureSelectorImp(investApi.getInstrumentsService()).getByTicker("KMM5");
        String accountId = new AccountResolverImpl("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();
        OrderService orderService = new OrderServiceImpl(accountId, investApi.getMarketDataService(), investApi.getOrdersService());
        orderService.limitBuyBestByOrderBook(new FutureHolder(future));
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}