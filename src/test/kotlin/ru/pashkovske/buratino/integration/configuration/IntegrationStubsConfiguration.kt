package ru.pashkovske.buratino.integration.configuration

import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.account.service.AccountSupplier
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.price.offer.service.OfferBookService
import ru.pashkovske.buratino.util.loader.FileLoader

@TestConfiguration
class IntegrationStubsConfiguration {
    @Bean
    @Primary
    fun offerBookService(): OfferBookService {
        return mock(OfferBookService::class.java)
    }

    @Bean
    @Primary
    fun instrumentServiceAdapter(): InstrumentServiceAdapter {
        return mock(InstrumentServiceAdapter::class.java)
    }

    @Bean
    @Primary
    fun extOrderServiceAdapter(): ExtOrderServiceAdapter {
        return mock(ExtOrderServiceAdapter::class.java)
    }

    @Bean
    @Primary
    fun accountSupplier(
        @Value("\${account.name}") accountName: String
    ): AccountSupplier {
        val accountSupplier: AccountSupplier = mock(AccountSupplier::class.java)
        `when`(accountSupplier.findAccount(accountName))
            .thenReturn(FileLoader.loadFromJson("stub/account/main.json", Account::class.java))
        return accountSupplier
    }
}
