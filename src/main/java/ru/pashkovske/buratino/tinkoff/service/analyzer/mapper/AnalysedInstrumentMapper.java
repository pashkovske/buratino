package ru.pashkovske.buratino.tinkoff.service.analyzer.mapper;

import ru.pashkovske.buratino.tinkoff.service.analyzer.model.CandleDto;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.InstrumentSpread;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.InstrumentWithSpreadDto;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.TradeDto;
import ru.pashkovske.buratino.tinkoff.service.common.utils.TimeUtils;
import ru.pashkovske.buratino.tinkoff.service.price.mapper.PriceMapper;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Trade;

public class AnalysedInstrumentMapper {
    public static InstrumentWithSpreadDto map(InstrumentSpread instrumentSpread) {
        return new InstrumentWithSpreadDto(
                instrumentSpread.instrument().getTicker(),
                instrumentSpread.instrument().getName(),
                instrumentSpread.instrument().getType(),
                instrumentSpread.spreadValue()
        );
    }

    public static CandleDto map(HistoricCandle candle) {
        return new CandleDto(
                PriceMapper.map(candle.getLow()),
                PriceMapper.map(candle.getHigh()),
                TimeUtils.map(candle.getTime()),
                candle.getVolume()
        );
    }

    public static TradeDto map(Trade trade) {
        return new TradeDto(
                TimeUtils.map(trade.getTime()),
                trade.getDirection(),
                trade.getQuantity(),
                PriceMapper.map(trade.getPrice()),
                trade.getTradeSource()
        );
    }
}
