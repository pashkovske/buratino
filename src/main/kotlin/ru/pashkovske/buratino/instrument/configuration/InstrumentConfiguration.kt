package ru.pashkovske.buratino.instrument.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.pashkovske.buratino.instrument.adapter.tinkoff.TinkoffInstrumentService
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.tinkoff.piapi.core.InvestApi

@Configuration
class InstrumentConfiguration {
    @Bean
    fun instrumentService(
        tinkoffInvestApi: InvestApi
    ): InstrumentService {
        return TinkoffInstrumentService(tinkoffInvestApi.instrumentsService)
    }
}