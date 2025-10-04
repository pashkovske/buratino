package ru.pashkovske.buratino.price.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.TinkoffOfferBookService
import ru.pashkovske.buratino.price.offer.repo.OfferBookRepo
import ru.pashkovske.buratino.price.offer.repo.OfferBookRepoInMemory
import ru.pashkovske.buratino.price.offer.service.MarketScrapper
import ru.pashkovske.buratino.price.offer.service.OfferBookService
import ru.pashkovske.buratino.price.price.service.CurrentMarketPriceService
import ru.pashkovske.buratino.price.price.service.MarketPriceService
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.MarketDataService
import ru.tinkoff.piapi.core.OrdersService

@Configuration
class PriceConfiguration {
    @Bean
    fun marketScraper(
        offerBookService: OfferBookService,
        offerBookRepo: OfferBookRepo
    ): MarketScrapper {
        return MarketScrapper(
            offerBookService = offerBookService,
            offerBookRepo = offerBookRepo
        )
    }
    
    @Bean
    fun marketPriceService(
        marketScrapper: MarketScrapper,
        offerBookRepo: OfferBookRepo
    ): MarketPriceService {
        return CurrentMarketPriceService(
            marketScrapper = marketScrapper,
            offerBookRepo = offerBookRepo
        )
    }
    
    @Bean
    fun offerBookRepo(): OfferBookRepo {
        return OfferBookRepoInMemory()
    }

    @Bean
    fun offerBookService(
        tinkoffMarketDataService: MarketDataService,
        tinkoffOrderService: OrdersService,
        account: Account
    ): OfferBookService {
        return TinkoffOfferBookService(
            tinkoffMarketDataService = tinkoffMarketDataService,
            tinkoffOrderService = tinkoffOrderService,
            account = account
        )
    }

    @Bean
    fun tinkoffMarketDataService(
        tinkoffInvestApi: InvestApi
    ): MarketDataService {
        return tinkoffInvestApi.marketDataService
    }
}