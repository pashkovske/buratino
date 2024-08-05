package ru.pashkovske.buratino;

import ru.pashkovske.buratino.tinkoff.service.account.AccountResolverImpl;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentOrdersByApi;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzer;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzerImpl;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.FutureWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelectorImpl;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderTinkoffOfficialApi;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderResponse;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.OrderStrategy;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.RobotExploitSpread;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestBuyPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.OtherRobotExploitWithSpread;
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.*;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

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
        SpreadAnalyzer spreadAnalyzer = new SpreadAnalyzerImpl(priceService, selector);
        OrderStrategy exploit = new RobotExploitSpread(
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
        /*for (String ticker : bigSpreadTickers) {
            command = new FollowBestBuyPrice(selector.getByTicker(ticker), 1);
            strategy.post(command);
        }*/
        command = new OtherRobotExploitWithSpread(
                selector.getByTicker("NKNCP"),
                2,
                Quotation.newBuilder().setUnits(69).setNano(840000000).build(),
                Quotation.newBuilder().setUnits(69).setNano(660000000).build()
        );
        command = new OtherRobotExploitWithSpread(
                selector.getByTicker("CHMK"),
                2,
                Quotation.newBuilder().setUnits(69).setNano(840000000).build(),
                Quotation.newBuilder().setUnits(69).setNano(660000000).build()
        );
        /*List<Trade> trades = marketDataServiceTinkoff.getLastTradesSync(
                selector.getByTicker("SBER").getFigi()/*,
                        Instant.now().minus(Duration.ofHours(49L)),
                        Instant.now().minus(Duration.ofHours(48L))
                ).stream()
                .peek(System.out::println)
                .toList();
        System.out.println(trades.size());
        //*/
        /*List<HistoricCandle> candles =  marketDataServiceTinkoff.getCandlesSync(
                selector.getByTicker("CoQ4").getId().id(),
                Instant.now().minus(Duration.ofDays(7L)),
                Instant.now(),
                CandleInterval.CANDLE_INTERVAL_4_HOUR
        );
        for (HistoricCandle candle : candles) {
            System.out.println(candle.getVolume());
        }
        //*/
        /*System.out.println(spreadAnalyzer.findBigSpreads(50,80, InstrumentType.INSTRUMENT_TYPE_FUTURES).stream()
                        .peek(spread -> {System.out.println(((FutureWrapper)spread.instrument()).getMargin());})
                .peek(System.out::println)
                .toList()
                .size()
        );
        //*/
        //exploit.post(command);

        while (true) {
            Thread.sleep(4000);
            List<Assignment> assignments = strategy.refreshAll();
            System.out.println("\n\n");
            for (Assignment assignment : assignments) {
                command = assignment.getCommand();
                OrderResponse order = assignment.getOrders().getFirst().getOrderResponse();

                System.out.println("| " +
                        command.getInstrument().getTicker() + "\t|\t" +
                        order.time() + "\t|\t" +
                        order.price().getUnits() + "." + order.price().getNano() + "\t|\t" +
                        order.direction() + "\t|\t" +
                        command.getInstrument().getName() + "\t|\t" +
                        order.commission().getUnits() + "." + order.commission().getNano()
                );
            }
            Thread.sleep(7000);
        }
        /*
        for (Future future : futures) {
            System.out.println(future.getName() + "\t|\t" + future.getTicker());
        }*/
    }
}