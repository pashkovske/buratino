package ru.pashkovske.buratino;

import ru.pashkovske.buratino.service.account.TinkoffAccountResolver;
import ru.pashkovske.buratino.service.instrument.TinkoffFutureSelector;
import ru.pashkovske.buratino.service.instrument.TinkoffShareSelector;
import ru.pashkovske.buratino.service.order.OrderService;
import ru.pashkovske.buratino.service.order.TinkoffOrderService;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        Future future = new TinkoffFutureSelector(investApi.getInstrumentsService()).getByTicker("KMM5");
        String accountId = new TinkoffAccountResolver("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();
        OrderService orderService = new TinkoffOrderService(accountId, investApi.getMarketDataService(), investApi.getOrdersService());
        orderService.buyBestByOrderBook(future);
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}