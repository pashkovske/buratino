package ru.pashkovske.buratino.price.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.MarketDataService

@Configuration
class PriceConfiguration {
    @Bean
    fun tinkoffMarketDataService(
        tinkoffInvestApi: InvestApi
    ): MarketDataService {
        return tinkoffInvestApi.marketDataService
    }
}