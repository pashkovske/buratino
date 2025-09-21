package ru.pashkovske.buratino.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import ru.tinkoff.piapi.core.InvestApi

@Configuration
class BuratinoServiceConfiguration {

    @Bean
    fun taskScheduler(): TaskScheduler {
        return ThreadPoolTaskScheduler()
    }

    @Bean
    fun investApi(
        @Value("\${tinkoff.token}") tinkoffToken: String
    ): InvestApi {
        if (tinkoffToken.isEmpty()) {
            throw IllegalArgumentException("Tinkoff token is empty")
        }
        return InvestApi.create(tinkoffToken)
    }
}
