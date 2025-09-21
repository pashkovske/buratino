package ru.pashkovske.buratino.account.confuguration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.pashkovske.buratino.account.adapter.tinkoff.TinkoffAccountSupplierApi
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.account.service.AccountSupplier
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.UsersService

@Configuration
class AccountConfiguration {
    @Bean
    fun account(
        accountSupplier: AccountSupplier,
        @Value("\${account.name}") accountName: String
    ): Account {
        if (accountName.isEmpty()) {
            throw IllegalArgumentException("Account name is empty")
        }
        return accountSupplier.findAccount(accountName) ?: throw IllegalStateException("Account not found")
    }

    @Bean
    fun accountSupplier(
        tinkoffUserService: UsersService
    ): AccountSupplier {
        return TinkoffAccountSupplierApi(tinkoffUserService)
    }

    @Bean
    fun tinkoffUserService(
        investApi: InvestApi
    ): UsersService {
        return investApi.userService
    }
}