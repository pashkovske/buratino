package ru.pashkovske.buratino.tinkoff.service.instrument.selector;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.FutureWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.ShareWrapper;

import java.util.List;

public interface InstrumentSelector {
    InstrumentWrapper getByName(String name);
    List<InstrumentWrapper> findByName(String name);
    InstrumentWrapper getByTicker(String ticker);
    InstrumentWrapper getById(InstrumentId id);
    List<ShareWrapper> getTradableShares();
    List<FutureWrapper> getTradableFutures();

}
