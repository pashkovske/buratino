package ru.pashkovske.buratino.tinkoff.service.market.instrument.selector;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.ShareHolder;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InstrumentsService;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class ShareSelector extends AbstractInstrumentSelector<Share> {
    private final InstrumentsService instrumentsService;

    @Override
    protected Stream<Share> getInstrumentStream() {
        return instrumentsService.getSharesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE).stream();
    }

    @Override
    protected ShareHolder buildInstrumentHolder(Share share) {
        return new ShareHolder(share);
    }
}
