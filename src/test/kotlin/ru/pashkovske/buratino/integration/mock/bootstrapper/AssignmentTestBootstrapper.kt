package ru.pashkovske.buratino.integration.mock.bootstrapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.ExtOrderServiceMocker
import ru.pashkovske.buratino.integration.mock.InstrumentServiceMocker
import ru.pashkovske.buratino.integration.mock.OfferBookMocker

@Service
class AssignmentTestBootstrapper(
    private val instrumentServiceMocker: InstrumentServiceMocker,
    private val extOrderServiceMocker: ExtOrderServiceMocker,
    private val offerBookServiceMocker: OfferBookMocker
) {
    fun prepareKZOSData(): InstrumentId {
        val iid = InstrumentId("a6121478-943f-4eae-bc2a-bab5c771dd4a")
        instrumentServiceMocker.addMock(
            path ="stub/instrument/share/kzos.json",
            iid = iid
        )
        offerBookServiceMocker.addMock(
            path = "stub/price/offer/kzos.json",
            depth = 5,
            iid = iid
        )
        extOrderServiceMocker.addCreateMock(
            pathRequest = "stub/order/create/kzos-request.json",
            pathResponse = "stub/order/create/kzos-response.json"
        )
        return iid
    }
}
