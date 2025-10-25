package ru.pashkovske.burationo.integration.configuration

import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import ru.tinkoff.piapi.core.InstrumentsService
import ru.tinkoff.piapi.core.MarketDataService
import ru.tinkoff.piapi.core.OrdersService
import ru.tinkoff.piapi.core.UsersService

@TestConfiguration
class TinkoffStubsConfiguration {
    @Bean
    @Primary
    fun tinkoffMarketDataService(): MarketDataService {
        return mock(MarketDataService::class.java)
    }

    @Bean
    @Primary
    fun instrumentsService(): InstrumentsService {
        return mock(InstrumentsService::class.java)
    }

    @Bean
    @Primary
    fun ordersService(): OrdersService {
        return mock(OrdersService::class.java)
    }

    @Bean
    @Primary
    fun usersService(): UsersService {
        return mock(UsersService::class.java)
    }
}