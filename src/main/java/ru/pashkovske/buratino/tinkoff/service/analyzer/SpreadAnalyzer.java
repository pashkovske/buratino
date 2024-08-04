package ru.pashkovske.buratino.tinkoff.service.analyzer;

import ru.pashkovske.buratino.tinkoff.service.analyzer.model.InstrumentSpread;
import ru.tinkoff.piapi.contract.v1.InstrumentType;

import java.util.List;

public interface SpreadAnalyzer {
    List<InstrumentSpread> findBigSpreads(
            long basicPointsThreshold,
            int limit,
            InstrumentType instrumentType
    );
}
