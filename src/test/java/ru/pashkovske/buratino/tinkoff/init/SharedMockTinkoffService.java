package ru.pashkovske.buratino.tinkoff.init;

import lombok.RequiredArgsConstructor;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;

import org.mockito.stubbing.Answer;
import ru.pashkovske.buratino.tinkoff.util.Deserializer;

import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OrdersService;
import ru.tinkoff.piapi.core.UsersService;

import java.util.List;

@RequiredArgsConstructor
public class SharedMockTinkoffService {
    private final Deserializer deserializer;

    public UsersService getUsersService() {
        UsersService usersService = mock(UsersService.class);

        when(usersService.getAccountsSync())
                .thenReturn(List.of(loadStub("account/main", Account.class)));

        return usersService;
    }

    public MarketDataService getMarketDataService() {
        MarketDataService marketDataService = mock(MarketDataService.class);

        when(marketDataService.getOrderBookSync("0b9afb23-280f-4fda-a7ad-816994959c6b", 1))
                .thenReturn(loadStub("order-book/has-spread/1st-deep/arenadat-group", GetOrderBookResponse.class));

        return marketDataService;
    }

    public InstrumentsService getInstrumentsService() {
        InstrumentsService instrumentsService = mock(InstrumentsService.class);

        when(instrumentsService.getShareByUidSync("0b9afb23-280f-4fda-a7ad-816994959c6b"))
                .thenReturn(loadStub("instrument/share/arenadat-group", Share.class));
        when(instrumentsService.findInstrumentSync("DATA"))
                .thenReturn(List.of(loadStub("instrument/share/arenadat-group", InstrumentShort.class)));

        return instrumentsService;
    }

    public OrdersService getOrdersService() {
        OrdersService ordersService = mock(OrdersService.class);

        when(ordersService.postLimitOrderSync(
                anyString(),
                anyLong(),
                any(Quotation.class),
                any(OrderDirection.class),
                anyString(),
                any(TimeInForceType.class),
                any(PriceType.class),
                anyString()
        )).thenAnswer(orderServicePostLimitMapper());

        return ordersService;
    }

    private <T> T loadStub(String path, Class<T> cls) {
        String tinkoffStubsPath = "stub/tinkoff/";
        return deserializer.deserialize(tinkoffStubsPath + path, cls);
    }

    private static Answer<PostOrderResponse> orderServicePostLimitMapper() {
        return invocation -> {
            String instrumentId = invocation.getArgument(0);
            long quantity = invocation.getArgument(1);
            Quotation price = invocation.getArgument(2);
            OrderDirection direction = invocation.getArgument(3);
            String orderId = invocation.getArgument(7);
            return PostOrderResponse.newBuilder()
                    .setOrderId("1234567890ABCD")
                    .setInstrumentUid(instrumentId)
                    .setInitialSecurityPrice(
                            MoneyValue.newBuilder()
                                    .setCurrency("rub")
                                    .setUnits(price.getUnits())
                                    .setNano(price.getNano())
                                    .build())
                    .setDirection(direction)
                    .setLotsRequested(quantity)
                    .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                    .setOrderRequestId(orderId)
                    .setExecutionReportStatus(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_NEW)
                    .build();
        };
    }
}
