package ru.pashkovske.buratino.tinkoff.service.price.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.tinkoff.piapi.contract.v1.*;
import ru.ttech.piapi.core.connector.streaming.ServerSideStreamConfiguration;
import ru.ttech.piapi.core.connector.streaming.ServerSideStreamWrapper;
import ru.ttech.piapi.core.connector.streaming.StreamServiceStubFactory;
import ru.ttech.piapi.core.connector.streaming.listeners.OnNextListener;

@RequiredArgsConstructor
@Slf4j
public class PriceChangesBrokerImpl implements PriceChangesBroker {
    // не используется, потому что нет обработки ошибок
    // private final MarketDataStreamManager tinkoffMarketDataStreamManager;
    private final StreamServiceStubFactory tinkoffStreamServiceFactory;
    // Допустимые значения: 1, 10, 20, 30, 40, 50
    // иначе выплёвывает ошибку SUBSCRIPTION_STATUS_DEPTH_IS_INVALID
    // см. https://developer.tbank.ru/invest/services/quotes/marketdata
    private final int ORDER_BOOK_DEPTH = 10;

    @Override
    public void subscribe(InstrumentWrapper instrument) {
        OrderBookInstrument requestInstrument = OrderBookInstrument.newBuilder()
                .setInstrumentId(instrument.getFigi())
                .setDepth(ORDER_BOOK_DEPTH)
                .build();
        SubscribeOrderBookRequest orderBookRequest = SubscribeOrderBookRequest.newBuilder()
                .setSubscriptionAction(SubscriptionAction.SUBSCRIPTION_ACTION_SUBSCRIBE)
                .addInstruments(requestInstrument)
                .build();
        MarketDataServerSideStreamRequest request = MarketDataServerSideStreamRequest.newBuilder()
                .setSubscribeOrderBookRequest(orderBookRequest)
                .build();

        ServerSideStreamWrapper<
                MarketDataStreamServiceGrpc.MarketDataStreamServiceStub,
                MarketDataResponse
                > serverSideStream = tinkoffStreamServiceFactory.newServerSideStream(
                ServerSideStreamConfiguration.builder(
                                MarketDataStreamServiceGrpc::newStub,
                                MarketDataStreamServiceGrpc.getMarketDataServerSideStreamMethod(),
                                (stub, observer) -> stub.marketDataServerSideStream(request, observer))
                        .addOnNextListener(tinkoffListener())
                        .addOnErrorListener(throwable -> System.out.println("Произошла ошибка: " + throwable.getMessage()))
                        .addOnCompleteListener(() -> System.out.println("Стрим завершен"))
                        .build()
        );
        serverSideStream.connect();

        log.info("Подписка на стакан инструмента {}", instrument.getTicker());
    }

    private OnNextListener<MarketDataResponse> tinkoffListener() {
        return markerDataResponse -> log.info("Обновление стакана инструмента ask: {}, bid: {}",
                markerDataResponse.getOrderbook().getAsks(0),
                markerDataResponse.getOrderbook().getBids(0));
    }
}
