package ru.pashkovske.buratino.tinkoff.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.pashkovske.buratino.tinkoff.service.account.*;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzer;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzerImpl;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelectorImpl;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderTinkoffOfficialApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.core.*;

@SuppressWarnings("unused")
@Configuration
public class BuratinoServiceConfiguration {

    @Bean
    public SpreadAnalyzer spreadAnalyzer(
            MarketPriceService priceService,
            InstrumentSelector selector
    ) {
        return new SpreadAnalyzerImpl(
                priceService,
                selector
        );
    }

    @Bean
    public CurrentAccountOrders currentAccountOrders(
            @Qualifier("brokerAccountId") String brokerAccountId,
            OrdersService tinkoffOrdersService
    ) {
        return new CurrentOrdersByApi(
                tinkoffOrdersService,
                brokerAccountId
        );
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public FollowBestPrice followBestPrice(
            OrderApi orderApi,
            MarketPriceService priceService,
            InstrumentSelector selector,
            TaskScheduler taskScheduler
    ) {
        return new FollowBestPrice(
                orderApi,
                priceService,
                selector,
                taskScheduler
        );
    }

    @Bean
    public OrderApi orderApi(
            @Qualifier("brokerAccountId") String brokerAccountId,
            OrdersService tinkoffOrderService
    ) {
        return new OrderTinkoffOfficialApi(
                brokerAccountId,
                tinkoffOrderService
        );
    }

    @Bean("brokerAccountId")
    public String brokerAccountId(AccountResolver accountResolver) {
        return accountResolver.getBrokerAccountId();
    }

    @Bean
    public AccountResolver accountResolver(UsersService tinkoffUserService) {
        String name ="Основной брокерский счет";
        return new AccountResolverImpl(
                name,
                tinkoffUserService
        );
    }

    @Bean
    public UsersService tinkoffUserService(InvestApi investApi) {
        return investApi.getUserService();
    }

    @Bean
    public InvestApi investApi(TinkoffCustomerConfig tinkoffCustomerConfig) {
        String fullAccessToken = System.getenv(tinkoffCustomerConfig.getTinkoffAccessToken());
        return InvestApi.create(fullAccessToken);
    }

    @Bean
    public OrdersService ordersService(InvestApi investApi) {
        return investApi.getOrdersService();
    }

    @Bean
    public MarketPriceService priceService(MarketDataService tinkoffMarketDateService) {
        return new CurrentMarketPriceService(tinkoffMarketDateService);
    }

    @Bean
    public MarketDataService tinkoffMarketDateService(InvestApi investApi) {
        return investApi.getMarketDataService();
    }

    @Bean
    public InstrumentSelector selector(InstrumentsService tinkoffInstrumentsService) {
        return new InstrumentSelectorImpl(tinkoffInstrumentsService);
    }

    @Bean
    public InstrumentsService tinkoffInstrumentsService(InvestApi investApi) {
        return investApi.getInstrumentsService();
    }
}
