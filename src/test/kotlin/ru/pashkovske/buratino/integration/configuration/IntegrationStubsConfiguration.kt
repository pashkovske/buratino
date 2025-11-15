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
import ru.pashkovske.buratino.integration.mock.ExtOrderServiceAdapterMock
import ru.pashkovske.buratino.integration.mock.OfferBookMock
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.price.offer.adapter.OfferBookAdapter
import ru.pashkovske.buratino.util.loader.FileLoader

@TestConfiguration
class IntegrationStubsConfiguration {
    @Bean
    @Primary
    fun offerBookService(
        offerBookMock: OfferBookMock
    ): OfferBookAdapter {
        return offerBookMock
    }

    @Bean
    fun offerBookMock(): OfferBookMock {
        return OfferBookMock()
    }

    @Bean
    @Primary
    fun instrumentServiceAdapter(): InstrumentServiceAdapter {
        return mock(InstrumentServiceAdapter::class.java)
    }

    @Bean
    @Primary
    fun extOrderServiceAdapter(
        offerBookMock: OfferBookMock
    ): ExtOrderServiceAdapter {
        return ExtOrderServiceAdapterMock(offerBookMock)
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
