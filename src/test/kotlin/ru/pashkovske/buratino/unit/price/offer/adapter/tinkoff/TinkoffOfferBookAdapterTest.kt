package ru.pashkovske.buratino.unit.price.offer.adapter.tinkoff

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.TinkoffOfferBookAdapter
import ru.tinkoff.piapi.core.MarketDataService
import ru.tinkoff.piapi.core.OrdersService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.whenever
import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.Share
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.model.QuotationLevelOffers
import ru.pashkovske.buratino.util.loader.FileLoader
import ru.tinkoff.piapi.contract.v1.OrderState as TinkoffOrderState
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse
import java.util.Objects

@ExtendWith(MockitoExtension::class)
class TinkoffOfferBookAdapterTest {

    @Mock
    private lateinit var marketDataService: MarketDataService
    @Mock
    private lateinit var ordersService: OrdersService
    @Mock
    private lateinit var instrumentService: InstrumentService

    private lateinit var tinkoffAdapter: TinkoffOfferBookAdapter

    private val account: Account = FileLoader.loadFromJson(
        path = "stub/account/main.json",
        clazz = Account::class.java
    )
    private val depth = 5
    private lateinit var iid: InstrumentId
    private lateinit var currency: Currency


    @BeforeEach
    fun setUp() {
        tinkoffAdapter = TinkoffOfferBookAdapter(
            tinkoffMarketDataService = marketDataService,
            tinkoffOrderService = ordersService,
            instrumentService = instrumentService,
            account = account
        )
    }

    private fun bootstrapOneLotShare() {
        val share: Share = FileLoader.loadFromJson(
            path = "stub/instrument/share/SBER.json",
            clazz = Share::class.java
        )
        iid = share.iid
        currency = share.currency
        whenever(instrumentService.get(iid)).thenReturn(share)

        val offerBook: GetOrderBookResponse = FileLoader.loadFromJson(
            path = "stub/tinkoff/market-data/offer-book/SBER.json",
            clazz = GetOrderBookResponse::class.java
        )
        whenever(marketDataService.getOrderBookSync(iid.id, depth)).thenReturn(offerBook)
    }

    private fun bootstrapManyLotShare() {
        val share: Share = FileLoader.loadFromJson(
            path = "stub/instrument/share/MAGN.json",
            clazz = Share::class.java
        )
        iid = share.iid
        currency = share.currency
        whenever(instrumentService.get(iid)).thenReturn(share)

        val offerBook: GetOrderBookResponse = FileLoader.loadFromJson(
            path = "stub/tinkoff/market-data/offer-book/MAGN.json",
            clazz = GetOrderBookResponse::class.java
        )
        whenever(marketDataService.getOrderBookSync(iid.id, depth)).thenReturn(offerBook)
    }

    private fun bootstrapFuture() {
        val future: Future = FileLoader.loadFromJson(
            path = "stub/instrument/future/MYH6.json",
            clazz = Future::class.java
        )
        iid = future.iid
        currency = future.currency
        whenever(instrumentService.get(iid)).thenReturn(future)

        val offerBook: GetOrderBookResponse = FileLoader.loadFromJson(
            path = "stub/tinkoff/market-data/offer-book/MYH6.json",
            clazz = GetOrderBookResponse::class.java
        )
        whenever(marketDataService.getOrderBookSync(iid.id, depth)).thenReturn(offerBook)
    }

    private fun bootstrapSelfOrders() {
        val myOrders: List<TinkoffOrderState> = FileLoader.loadListFromJson(
            path = "stub/tinkoff/orders/my-orders.json",
            clazz = TinkoffOrderState::class.java
        )
        whenever(ordersService.getOrdersSync(account.id)).thenReturn(myOrders)
    }

    @Test
    fun `share no self orders`() {
        bootstrapOneLotShare()
        whenever(ordersService.getOrdersSync(account.id)).thenReturn(emptyList())

        val result: OfferBook = tinkoffAdapter.getOfferBook(iid, depth)

        // Then
        assertEquals(5, result.asks.size)
        val lowestAsk: Price = result.asks.keys.min()
        val expectedLowestAsk = Price(296L, 650_000_000, currency)
        assertEquals(expectedLowestAsk, lowestAsk)
        assertTrue(
            result.asks.values
                .map(QuotationLevelOffers::selfAffiliated)
                .all(Objects::isNull)
        )
        assertTrue(
            result.asks.values
                .map(QuotationLevelOffers::alienAffiliated)
                .all(Objects::nonNull)
        )

        assertEquals(5, result.bids.size)
        val highestBid: Price = result.bids.keys.max()
        val expectedHighestBid = Price(296L, 510_000_000, currency)
        assertEquals(expectedHighestBid, highestBid)
        assertTrue(
            result.bids.values
                .map(QuotationLevelOffers::selfAffiliated)
                .all(Objects::isNull)
        )
        assertTrue(
            result.bids.values
                .map(QuotationLevelOffers::alienAffiliated)
                .all(Objects::nonNull)
        )
    }
    
    @Test
    fun `share with self orders`() {
        bootstrapOneLotShare()
        bootstrapSelfOrders()

        val result: OfferBook = tinkoffAdapter.getOfferBook(iid, depth)
        
        // Then
        val expectedSelfOrderPrice = Price(
            unit = 296L,
            nano = 680_000_000,
            currency = currency
        )
        assertEquals(5, result.asks.size)
        assertEquals(5, result.bids.size)
        val selfPriceLevelOffers: QuotationLevelOffers? = result.asks[expectedSelfOrderPrice]
        assertNotNull(selfPriceLevelOffers)

        assertNotNull(selfPriceLevelOffers!!.selfAffiliated)
        assertEquals(1, selfPriceLevelOffers.selfAffiliated!!.lots)

        assertNotNull(selfPriceLevelOffers.alienAffiliated)
        assertEquals(1, selfPriceLevelOffers.alienAffiliated!!.lots)
    }

    @Test
    fun `future no self orders`() {
        bootstrapFuture()
        whenever(ordersService.getOrdersSync(account.id)).thenReturn(emptyList())

        val result: OfferBook = tinkoffAdapter.getOfferBook(iid, depth)

        // Then
        assertEquals(5, result.asks.size)
        val lowestAsk: Price = result.asks.keys.min()
        val expectedLowestAsk = Price(11874L, 706_960_000, currency)
        assertEquals(expectedLowestAsk, lowestAsk)
        assertTrue(
            result.asks.values
                .map(QuotationLevelOffers::selfAffiliated)
                .all(Objects::isNull)
        )
        assertTrue(
            result.asks.values
                .map(QuotationLevelOffers::alienAffiliated)
                .all(Objects::nonNull)
        )

        assertEquals(5, result.bids.size)
        val highestBid: Price = result.bids.keys.max()
        val expectedHighestBid = Price(11690L, 373_640_000, currency)
        assertEquals(expectedHighestBid, highestBid)
        assertTrue(
            result.bids.values
                .map(QuotationLevelOffers::selfAffiliated)
                .all(Objects::isNull)
        )
        assertTrue(
            result.bids.values
                .map(QuotationLevelOffers::alienAffiliated)
                .all(Objects::nonNull)
        )
    }

    @Test
    fun `future with self orders`() {
        bootstrapFuture()
        bootstrapSelfOrders()

        val result: OfferBook = tinkoffAdapter.getOfferBook(iid, depth)

        // Then
        val expectedSelfOrderPrice = Price(
            unit = 11690L,
            nano = 373_640_000,
            currency = currency
        )
        assertEquals(5, result.asks.size)
        assertEquals(5, result.bids.size)
        val selfPriceLevelOffers: QuotationLevelOffers? = result.bids[expectedSelfOrderPrice]
        assertNotNull(selfPriceLevelOffers)

        assertNotNull(selfPriceLevelOffers!!.selfAffiliated)
        assertEquals(1, selfPriceLevelOffers.selfAffiliated!!.lots)

        assertNotNull(selfPriceLevelOffers.alienAffiliated)
        assertEquals(3, selfPriceLevelOffers.alienAffiliated!!.lots)
    }

    @Test
    fun `share with many slots and self orders`() {
        bootstrapManyLotShare()
        bootstrapSelfOrders()

        val result: OfferBook = tinkoffAdapter.getOfferBook(iid, depth)

        // Then
        val expectedSelfOrderPrice = Price(
            unit = 26L,
            nano = 460_000_000,
            currency = currency
        )
        assertEquals(5, result.asks.size)
        assertEquals(5, result.bids.size)
        val selfPriceLevelOffers: QuotationLevelOffers? = result.bids[expectedSelfOrderPrice]
        assertNotNull(selfPriceLevelOffers)
        assertNotNull(selfPriceLevelOffers!!.selfAffiliated)
        assertEquals(1, selfPriceLevelOffers.selfAffiliated!!.lots)
        assertNotNull(selfPriceLevelOffers.alienAffiliated)
        assertEquals(100, selfPriceLevelOffers.alienAffiliated!!.lots)
    }
}
