package ru.pashkovske.buratino.integration.mock.bootstrapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.InstrumentServiceMocker
import ru.pashkovske.buratino.integration.mock.OfferBookMocker

@Service
class AssignmentTestBootstrapper(
    private val instrumentServiceMocker: InstrumentServiceMocker,
    private val offerBookServiceMocker: OfferBookMocker
) {
    fun prepareKZOSCreateTopSell(): InstrumentId {
        val iid = InstrumentId("a6121478-943f-4eae-bc2a-bab5c771dd4a")
        instrumentServiceMocker.addMock(
            path ="stub/instrument/share/kzos.json",
            iid = iid
        )
        offerBookServiceMocker.addMock(
            path = "stub/price/offer/without-self/kzos.json",
            depth = 5,
            iid = iid
        )
        return iid
    }

    fun prepareKZOSRefreshTopSell(): InstrumentId {
        val iid = InstrumentId("a6121478-943f-4eae-bc2a-bab5c771dd4a")
        instrumentServiceMocker.addMock(
            path ="stub/instrument/share/kzos.json",
            iid = iid
        )
        offerBookServiceMocker.addMock(
            path = "stub/price/offer/with-self/kzos.json",
            depth = 5,
            iid = iid
        )
        return iid
    }

    fun prepareKZOSCreateFractionalSpreadBuy(): InstrumentId {
        val iid = InstrumentId("a6121478-943f-4eae-bc2a-bab5c771dd4a")
        instrumentServiceMocker.addMock(
            path ="stub/instrument/share/kzos.json",
            iid = iid
        )
        offerBookServiceMocker.addMock(
            path = "stub/price/offer/without-self/kzos.json",
            depth = 5,
            iid = iid
        )
        return iid
    }

    fun prepareKZOSRefreshFractionalSpreadBuy(): InstrumentId {
        val iid = InstrumentId("a6121478-943f-4eae-bc2a-bab5c771dd4a")
        instrumentServiceMocker.addMock(
            path ="stub/instrument/share/kzos.json",
            iid = iid
        )
        offerBookServiceMocker.addMock(
            path = "stub/price/offer/with-self/kzos.json",
            depth = 5,
            iid = iid
        )
        return iid
    }
}
