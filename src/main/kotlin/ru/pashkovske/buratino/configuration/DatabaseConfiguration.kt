package ru.pashkovske.buratino.configuration

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.OPTIONS
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.DATABASE
import io.r2dbc.spi.ConnectionFactoryOptions.DRIVER
import io.r2dbc.spi.ConnectionFactoryOptions.HOST
import io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD
import io.r2dbc.spi.ConnectionFactoryOptions.PORT
import io.r2dbc.spi.ConnectionFactoryOptions.USER
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(
    value = [
        "ru.pashkovske.buratino.assignment",
        "ru.pashkovske.buratino.order.repo"
    ]
)
class DatabaseConfiguration : AbstractR2dbcConfiguration() {

    override fun connectionFactory(): ConnectionFactory {
        val options: Map<String, String> = mapOf(
            "lock_timeout" to "10s",
            "statement_timeout" to "5s",
        )

        val connectionFactory: ConnectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "buratino_user")
                .option(PASSWORD, "buratino_password")
                .option(DATABASE, "buratino_db")
                .option(OPTIONS, options)
                .build()
        )

        return connectionFactory
    }

    @Bean
    fun r2dbcEntityTemplate(): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory())
    }
}
