package ru.pashkovske.buratino.service.instrument;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InstrumentsService;

@RequiredArgsConstructor
public class TinkoffShareSelector implements ShareSelector {
    final InstrumentsService instrumentsService;

    @Override
    public Share findSingleByName(String name) {
        return instrumentsService.getSharesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE).stream()
                .filter(share -> share.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
