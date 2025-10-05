package ru.pashkovske.buratino.instrument.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.piapi.core.InstrumentsService
import ru.tinkoff.piapi.core.InvestApi

@Configuration
class InstrumentConfiguration {
    @Bean
    fun instrumentsService(
        tinkoffInvestApi: InvestApi
    ): InstrumentsService {
        return tinkoffInvestApi.instrumentsService
    }
}
