package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentOrdersByApi;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzer;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzerImpl;
import ru.pashkovske.buratino.tinkoff.service.controller.AnalyzerController;
import ru.pashkovske.buratino.tinkoff.service.controller.AssignmentController;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelectorImpl;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderTinkoffOfficialApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.OrderStrategy;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.RobotExploitSpread;
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.core.*;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);
        OrdersService tinkoffOrderService = investApi.getOrdersService();
        MarketDataService marketDataServiceTinkoff = investApi.getMarketDataService();
        InstrumentsService instrumentsService = investApi.getInstrumentsService();

        String accountId = new AccountResolverImpl("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();
        CurrentAccountOrders currentAccountOrders = new CurrentOrdersByApi(investApi.getOrdersService(), accountId);
        OrderApi orderMaker = new OrderTinkoffOfficialApi(
                accountId,
                tinkoffOrderService
        );
        MarketPriceService priceService = new CurrentMarketPriceService(marketDataServiceTinkoff);
        InstrumentSelector selector = new InstrumentSelectorImpl(instrumentsService);
        FollowBestPrice followBestPrice = new FollowBestPrice(
                orderMaker,
                priceService,
                selector
        );
        SpreadAnalyzer spreadAnalyzer = new SpreadAnalyzerImpl(priceService, selector);
        RobotExploitSpread exploitStrategy = new RobotExploitSpread(
                orderMaker,
                priceService,
                selector
        );

        AnalyzerController analyzerController = new AnalyzerController(
                marketDataServiceTinkoff,
                selector,
                spreadAnalyzer
        );
        AssignmentController assignmentController = new AssignmentController(
                followBestPrice,
                exploitStrategy,
                currentAccountOrders,
                selector
        );

        //analyzerController.getBigSpreadFutures(50, 50);
        //analyzerController.getLastTrades("SBER",1);
        assignmentController.pullAllForBestPrice();
        //assignmentController.postBuy(List.of("CBOM"));
        while (true) {
            Thread.sleep(4000);
            assignmentController.pingBestPrice();
            Thread.sleep(7000);
        }
        //*/
    }
}