package ru.pashkovske.buratino.tinkoff.service.analyzer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pashkovske.buratino.tinkoff.service.analyzer.SpreadAnalyzer;
import ru.pashkovske.buratino.tinkoff.service.analyzer.mapper.AnalysedInstrumentMapper;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.CandleDto;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.InstrumentWithSpreadDto;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.TradeDto;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.contract.v1.Trade;
import ru.tinkoff.piapi.core.MarketDataService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequiredArgsConstructor
public class AnalyzerController {
    private final MarketDataService marketDataServiceTinkoff;
    private final InstrumentSelector selector;
    private final SpreadAnalyzer spreadAnalyzer;

    @GetMapping("/instrument/{ticker}/trades")
    public List<TradeDto> getLastTrades(@PathVariable String ticker, @RequestParam long lastHours) {
        return marketDataServiceTinkoff.getLastTradesSync(
                        selector.getByTicker(ticker).getFigi(),
                        Instant.now().minus(Duration.ofHours(lastHours)),
                        Instant.now()
                ).stream()
                .map(AnalysedInstrumentMapper::map)
                .toList();
    }

    @GetMapping("/instrument/{ticker}/candles")
    public List<CandleDto> getCandles(@PathVariable String ticker, @RequestParam long lastDays) {
        return marketDataServiceTinkoff.getCandlesSync(
                        selector.getByTicker(ticker).getId().id(),
                        Instant.now().minus(Duration.ofDays(lastDays)),
                        Instant.now(),
                        CandleInterval.CANDLE_INTERVAL_4_HOUR
                )
                .stream()
                .map(AnalysedInstrumentMapper::map)
                .toList();
    }

    @GetMapping("/instrument-list/future/big-spread")
    public List<InstrumentWithSpreadDto> getBigSpreadFutures(@RequestParam long threshold, @RequestParam int limit) {
        return spreadAnalyzer.findBigSpreads(
                        threshold,
                        limit,
                        InstrumentType.INSTRUMENT_TYPE_FUTURES
                )
                .stream()
                .map(AnalysedInstrumentMapper::map)
                .toList();
    }

    @GetMapping("/instrument-list/share/big-spread")
    public List<InstrumentWithSpreadDto> getBigSpreadShares(@RequestParam long threshold, @RequestParam int limit) {
        return spreadAnalyzer.findBigSpreads(
                        threshold,
                        limit,
                        InstrumentType.INSTRUMENT_TYPE_SHARE
                )
                .stream()
                .map(AnalysedInstrumentMapper::map)
                .toList();
    }
}
