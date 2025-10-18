package ru.pashkovske.buratino.tinkoff.configuration;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.pashkovske.buratino.account.AccountResolver;
import ru.pashkovske.buratino.account.AccountResolverImpl;
import ru.pashkovske.buratino.account.CurrentAccountOrders;
import ru.pashkovske.buratino.account.CurrentOrdersByApi;
import ru.pashkovske.buratino.tinkoff.init.SharedMockTinkoffService;
import ru.pashkovske.buratino.analyzer.SpreadAnalyzer;
import ru.pashkovske.buratino.analyzer.SpreadAnalyzerImpl;
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter;
import ru.pashkovske.buratino.instrument.adapter.tinkoff.TinkoffInstrumentServiceAdapter;
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter;
import ru.pashkovske.buratino.order.adapter.tinkoff.TinkoffOrderApi;
import ru.pashkovske.buratino.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.price.quotation.service.CurrentMarketPriceService;
import ru.pashkovske.buratino.price.quotation.service.MarketPriceService;
import ru.pashkovske.buratino.tinkoff.util.Deserializer;
import ru.pashkovske.buratino.tinkoff.util.FileLoader;
import ru.tinkoff.piapi.core.*;

@SuppressWarnings("unused")
@TestConfiguration
public class AppTestConfiguration {

    @Bean
    public SpreadAnalyzer spreadAnalyzer(
            MarketPriceService priceService,
            InstrumentServiceAdapter selector
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
        return Mockito.mock(ThreadPoolTaskScheduler.class);
    }

    @Bean
    public FollowBestPrice followBestPrice(
            ExtOrderServiceAdapter orderApi,
            MarketPriceService priceService,
            InstrumentServiceAdapter selector,
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
    public ExtOrderServiceAdapter orderApi(
            @Qualifier("brokerAccountId") String brokerAccountId,
            OrdersService tinkoffOrderService
    ) {
        return new TinkoffOrderApi(
                brokerAccountId,
                tinkoffOrderService
        );
    }

    @Bean("brokerAccountId")
    public String brokerAccountId(AccountResolver accountResolver) {
        return accountResolver.brokerAccountId;
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
    public MarketPriceService priceService(MarketDataService tinkoffMarketDateService) {
        return new CurrentMarketPriceService(tinkoffMarketDateService);
    }

    @Bean
    public InstrumentServiceAdapter selector(InstrumentsService tinkoffInstrumentsService) {
        return new TinkoffInstrumentServiceAdapter(tinkoffInstrumentsService);
    }

    @Bean
    public Deserializer deserializer(FileLoader fileLoader) {
        return new Deserializer(fileLoader);
    }

    @Bean
    public FileLoader fileLoader() {
        return new FileLoader( "src/test/resources/");
    }

    @Bean
    public SharedMockTinkoffService sharedMockTinkoffService(Deserializer deserializer) {
        return new SharedMockTinkoffService(deserializer);
    }

    @Bean
    @ConditionalOnMissingBean(InstrumentsService.class)
    public InstrumentsService mockTinkoffInstrumentsService(SharedMockTinkoffService sharedMockTinkoffService) {
        return sharedMockTinkoffService.getInstrumentsService();
    }

    @Bean
    @ConditionalOnMissingBean(MarketDataService.class)
    public MarketDataService mockTinkoffMarketDataService(SharedMockTinkoffService sharedMockTinkoffService) {
        return sharedMockTinkoffService.getMarketDataService();
    }

    @Bean
    @ConditionalOnMissingBean(UsersService.class)
    public UsersService mockTinkoffUsersService(SharedMockTinkoffService sharedMockTinkoffService) {
        return sharedMockTinkoffService.getUsersService();
    }

    @Bean
    @ConditionalOnMissingBean(OrdersService.class)
    public OrdersService mockTinkoffOrderService(SharedMockTinkoffService sharedMockTinkoffService) {
        return sharedMockTinkoffService.getOrdersService();
    }
}
