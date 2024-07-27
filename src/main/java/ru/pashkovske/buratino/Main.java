package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.instrument.FutureSelector;
import ru.pashkovske.buratino.tinkoff.service.instrument.ShareSelector;
import ru.pashkovske.buratino.tinkoff.service.model.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.order.OrderService;
import ru.pashkovske.buratino.tinkoff.service.order.OrderServiceImpl;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.*;

public class Main {
    public static void main(String[] args) {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        InstrumentHolder<Share> share = new ShareSelector(investApi.getInstrumentsService()).getByTicker("UWGN");
        String accountId = new AccountResolverImpl("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();
        OrderService orderService = new OrderServiceImpl(accountId, investApi.getMarketDataService(), investApi.getOrdersService());
        orderService.limitBuyBestByOrderBook(share);
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}