package ru.pashkovske.buratino.order.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.OrdersService

@Configuration
class OrderConfiguration {
    @Bean
    fun tinkoffOrderService(
        tinkoffInvestApi: InvestApi
    ): OrdersService {
        return tinkoffInvestApi.ordersService
    }
}