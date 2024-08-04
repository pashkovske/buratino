package ru.pashkovske.buratino.tinkoff.service.instrument.selector;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.FutureWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.ShareWrapper;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.core.InstrumentsService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InstrumentSelectorImpl implements InstrumentSelector {
    private final InstrumentsService instrumentsService;

    @Override
    public InstrumentWrapper getByName(String name) {
        return map(findExactlyOne(name));
    }

    @Override
    public List<InstrumentWrapper> findByName(String name) {
        return findInstruments(name).stream().map(this::map).toList();
    }

    @Override
    public InstrumentWrapper getByTicker(String ticker) {
        List<InstrumentShort> instruments = instrumentsService.findInstrumentSync(ticker).stream()
                .filter(InstrumentShort::getApiTradeAvailableFlag)
                .filter(instrument -> instrument.getTicker().equals(ticker))
                .toList();
        if (instruments.size() != 1) {
            throw new IllegalStateException("Найден не 1 инструмента\n" +
                    instruments.stream()
                            .map(Objects::toString)
                            .collect(Collectors.joining())
            );
        }
        return map(instruments.getFirst());
    }

    @Override
    public InstrumentWrapper getById(InstrumentId id) {
        return map(findExactlyOne(id.id()));
    }

    @Override
    public List<ShareWrapper> getTradableShares() {
        return instrumentsService.getTradableSharesSync().stream()
                .map(ShareWrapper::new)
                .filter(share -> !share.getForQualInvestorFlag() && share.isTradableNow())
                .toList();
    }

    @Override
    public List<FutureWrapper> getTradableFutures() {
        return instrumentsService.getTradableFuturesSync().stream()
                .map(FutureWrapper::new)
                .filter(share -> !share.getForQualInvestorFlag() && share.isTradableNow())
                .toList();
    }

    private List<InstrumentShort> findInstruments(String query) {
        return instrumentsService.findInstrumentSync(query)
                .stream()
                .filter(InstrumentShort::getApiTradeAvailableFlag)
                .toList();
    }

    private InstrumentShort findExactlyOne(String query) {
        List<InstrumentShort> instruments = findInstruments(query);
        if (instruments.size() != 1) {
            throw new IllegalStateException("Найден не 1 инструмента\n" +
                    instruments.stream()
                            .map(Objects::toString)
                            .collect(Collectors.joining())
            );
        }
        return instruments.getFirst();
    }

    private InstrumentWrapper map(InstrumentShort instrumentShort) {
        if (instrumentShort.getInstrumentKind().equals(InstrumentType.INSTRUMENT_TYPE_FUTURES)) {
            return new FutureWrapper(instrumentsService.getFutureByUidSync(instrumentShort.getUid()));
        } else if (instrumentShort.getInstrumentKind().equals(InstrumentType.INSTRUMENT_TYPE_SHARE)) {
            return new ShareWrapper(instrumentsService.getShareByUidSync(instrumentShort.getUid()));
        }
        else {
            throw new IllegalStateException("Не реализованный вариант инструмента: " + instrumentShort.getInstrumentKind());
        }
    }
}
