package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.price.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.market.instrument.selector.ShareSelector;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.SimpleByeOneCommand;
import ru.pashkovske.buratino.tinkoff.service.model.order.SimpleSellOneCommand;
import ru.pashkovske.buratino.tinkoff.service.order.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.OrderStrategy;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.StaticBestOrder;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.*;

public class Main {
    public static void main(String[] args) {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        InstrumentHolder<Share> share = new ShareSelector(investApi.getInstrumentsService()).getByTicker("MGKL");
        String accountId = new AccountResolverImpl("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();
        OrderStrategy orderMaker = new StaticBestOrder(new OrderApi(accountId, investApi.getOrdersService()), new CurrentMarketPriceService(investApi.getMarketDataService()));
        orderMaker.postOrder(new SimpleByeOneCommand(share));
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}