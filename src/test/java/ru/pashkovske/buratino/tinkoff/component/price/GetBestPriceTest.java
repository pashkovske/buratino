package ru.pashkovske.buratino.tinkoff.component.price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.springframework.context.annotation.Import;
import ru.pashkovske.buratino.tinkoff.configuration.AppTestConfiguration;
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.MarketDataService;

import ru.pashkovske.buratino.tinkoff.service.instrument.model.ShareWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.pashkovske.buratino.tinkoff.util.Deserializer;

@SpringBootTest(
        classes = {
                MarketPriceService.class
        }
)
@Import(AppTestConfiguration.class)
public class GetBestPriceTest {
    @MockBean
    private MarketDataService marketDataService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Autowired
    private Deserializer deserializer;

    @Test
    void noExcludeOrders() {
        InstrumentWrapper instrument = deserializer.deserialize("stub/buratino/instrument/wrapper/share/arenadat-group", ShareWrapper.class);
        GetOrderBookResponse orderBookResponse = deserializer.deserialize("stub/tinkoff/order-book/has-spread/1st-deep/arenadat-group", GetOrderBookResponse.class);

        when(marketDataService.getOrderBookSync(instrument.getId().id(), 1)).thenReturn(orderBookResponse);

        Quotation bestBuyPriceRetrieved = marketPriceService.getBestPrice(instrument, OrderDirection.ORDER_DIRECTION_BUY);
        Quotation bestBuyPriceExpected = deserializer.deserialize("response/price/best-fastest/arenadat-group/buy", Quotation.class);
        assertEquals(bestBuyPriceExpected, bestBuyPriceRetrieved);

        Quotation bestSellPrice = marketPriceService.getBestPrice(instrument, OrderDirection.ORDER_DIRECTION_SELL);
        Quotation bestSellPriceExpected = deserializer.deserialize("response/price/best-fastest/arenadat-group/sell", Quotation.class);
        assertEquals(bestSellPriceExpected, bestSellPrice);
    }
}
