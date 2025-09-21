package ru.pashkovske.buratino.order.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.order.adapter.tinkoff.TinkoffOrderApi
import ru.pashkovske.buratino.order.service.OrderService
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.OrdersService

@Configuration
class OrderConfiguration {
    @Bean
    fun orderService(
        tinkoffOrdersService: OrdersService,
        account: Account
    ): OrderService {
        return TinkoffOrderApi(
            tinkoffOrderService = tinkoffOrdersService,
            account = account
        )
    }

    @Bean
    fun tinkoffOrderService(
        tinkoffInvestApi: InvestApi
    ): OrdersService {
        return tinkoffInvestApi.ordersService
    }
}