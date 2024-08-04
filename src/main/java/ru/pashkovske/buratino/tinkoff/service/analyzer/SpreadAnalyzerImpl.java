package ru.pashkovske.buratino.tinkoff.service.analyzer;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.analyzer.model.InstrumentSpread;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class SpreadAnalyzerImpl implements SpreadAnalyzer {
    private final MarketPriceService priceService;
    private final InstrumentSelector instrumentSelector;
    private final String[] ALWAYS_POPULAR_TICKERS_TO_EXCLUDE = {
            "UNAC",
            "VKCO",
            "TTLK",
            "MGNT",
            "SVCB",
            "ETLN",
            "KZOSP",
            "UGLD",
            "SELG",
            "TATNP",
            "GTRK",
            "PRFN",
            "MAGN",
            "VTBR",
            "RUAL",
            "NKHP",
            "OKEY",
            "ALRS",
            "MRKP",
            "FLOT",
            "TATN",
            "UWGN",
            "DVEC",
            "MTSS",
            "TGKN",
            "TRNFP",
            "FEES",
            "IRAO",
            "GCHE",
            "SNGSP",
            "NVTK",
            "AQUA",
            "MBNK",
            "MOEX",
            "ROLO",
            "OZON",
            "OGKB",
            "SNGS",
            "ABRD",
            "PIKK",
            "ROSN",
            "EUTR",
            "TRMK",
            "MRKU",
            "CHMF",
            "ENPG",
            "MRKV",
            "TCSG",
            "CNTL",
            "RTKMP",
            "KMAZ",
            "FIXP",
            "MGTSP",
            "YDEX",
            "SVAV",
            "AGRO",
            "SMLT",
            "BANE",
            "AFLT",
            "CIAN",
            "SBER",
            "GECO",
            "MVID",
            "PMSBP",
            "MTLR",
            "GAZP",
            "SBERP",
            "LENT",
            "RENI",
            "POSI",
            "SIBN",
            "RNFT",
            "QIWI",
            "HYDR",
            "NLMK",
            "BELU",
            "SFIN",
            "RASP",
            "SGZH",
            "LSNGP",
            "GMKN",
            "LIFE",
            "UPRO",
            "MSNG",
            "AFKS",
            "PLZL",
            "LKOH",
            "NMTP",
            "BANEP",
            "MSRS",
            "ASTR",
            "CNTLP",
            "MTLRP"
    };

    @Override
    public List<InstrumentSpread> findBigSpreads(long basicPointsThreshold, int limit) {
        return instrumentSelector.getTradableShares().stream()
                .filter(instrument ->
                        Arrays.stream(ALWAYS_POPULAR_TICKERS_TO_EXCLUDE).noneMatch(exclude ->
                                exclude.equals(instrument.getTicker())
                        )
                )
                .limit(limit)
                .map(instrument -> new InstrumentSpread(instrument, priceService.getSpreadBasisPoints(instrument)))
                .filter(spread -> spread.spreadValue() > basicPointsThreshold)
                .sorted()
                .toList()
                .reversed()
                ;
    }

    private boolean spreadIsAboveThreshold(InstrumentWrapper instrument, long threshold) {
        long spread = priceService.getSpreadBasisPoints(instrument);
        boolean isAbove = spread > threshold;
        if (isAbove) {
            System.out.println(instrument.getName() + ": " + spread);
        }
        /*if (spread * 2 < threshold) {
            System.out.println(instrument.getTicker());
        }*/
        return isAbove;
    }
}
