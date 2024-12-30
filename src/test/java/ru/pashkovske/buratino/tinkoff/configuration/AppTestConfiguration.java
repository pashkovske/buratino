package ru.pashkovske.buratino.tinkoff.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentMarketPriceService;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.core.*;

@SuppressWarnings("unused")
@TestConfiguration
public class AppTestConfiguration {

    @Bean
    public MarketPriceService priceService(MarketDataService tinkoffMarketDateService) {
        return new CurrentMarketPriceService(tinkoffMarketDateService);
    }
}
