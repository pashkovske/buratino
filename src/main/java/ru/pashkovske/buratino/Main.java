package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentOrdersByApi;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelectorImpl;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderTinkoffOfficialApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.OrderStrategy;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestBuyPrice;
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.contract.v1.OrderState;
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
        OrderStrategy strategy = new FollowBestPrice(
                orderMaker,
                priceService,
                selector
        );

        List<OrderState> orders = currentAccountOrders.getAllOrders();
        System.out.println(strategy.pull(orders));

        List<String> bigSpreadTickers = List.of(
                //"PMSBP", // Пермэнергосбыт
                //"KAZTP", // КуйбышевВзот - привелегированные
                "RBCM", // РБК
                "LSNG", // Россети Ленэнерго
                "KZOS" // Казанский органический синтез
                //"SVCB" // Совкомбанк
        );
        AssignmentCommand command;
        for (String ticker : bigSpreadTickers) {
            command = new FollowBestBuyPrice(selector.getByTicker(ticker), 1);
            strategy.post(command);
        }

        while (true) {
            Thread.sleep(3000);
            List<Assignment> assignments = strategy.refreshAll();
            System.out.println("\n\n");
            for (Assignment assignment : assignments) {
                System.out.println(assignment);
            }
            Thread.sleep(40000);
        }
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}