package ru.pashkovske.buratino.tinkoff.service.controller;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzer;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.FutureWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.contract.v1.Trade;
import ru.tinkoff.piapi.core.MarketDataService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class AnalyzerController {
    private final MarketDataService marketDataServiceTinkoff;
    private final InstrumentSelector selector;
    private final SpreadAnalyzer spreadAnalyzer;

    public void getLastTrades(String ticker, long lastHours) {
        List<Trade> trades = marketDataServiceTinkoff.getLastTradesSync(
                        selector.getByTicker(ticker).getFigi(),
                        Instant.now().minus(Duration.ofHours(lastHours)),
                        Instant.now()
                ).stream()
                .peek(System.out::println)
                .toList();
        System.out.println(trades.size());
    }

    public void getCandles(String ticker, long lastDays) {
        List<HistoricCandle> candles =  marketDataServiceTinkoff.getCandlesSync(
                selector.getByTicker(ticker).getId().id(),
                Instant.now().minus(Duration.ofDays(lastDays)),
                Instant.now(),
                CandleInterval.CANDLE_INTERVAL_4_HOUR
        );
        for (HistoricCandle candle : candles) {
            System.out.println(candle.getVolume());
        }
    }

    public void getBigSpreadFutures(long basicPtsThreshold, int limit) {
        System.out.println(
                spreadAnalyzer.findBigSpreads(
                                basicPtsThreshold,
                                limit,
                                InstrumentType.INSTRUMENT_TYPE_FUTURES
                        )
                        .stream()
                        .peek(spread -> System.out.println(
                                ((FutureWrapper) spread.instrument()).getMargin()
                        ))
                        .peek(System.out::println)
                        .toList()
                        .size()
        );
    }
}
