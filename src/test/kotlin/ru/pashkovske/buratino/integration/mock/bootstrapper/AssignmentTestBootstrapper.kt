package ru.pashkovske.buratino.integration.mock.bootstrapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.InstrumentServiceMocker
import ru.pashkovske.buratino.integration.mock.OfferBookMock
import ru.pashkovske.buratino.util.loader.FileLoader

@Service
class AssignmentTestBootstrapper(
    private val instrumentServiceMocker: InstrumentServiceMocker,
    private val offerBookServiceMock: OfferBookMock
) {
    fun bootstrapInstrumentServiceMocker() {
        val instrumentStubsPath = "stub/instrument/share"
        val shareStubPaths: Set<String> = FileLoader.walkPath(instrumentStubsPath)
        shareStubPaths
            .map { "$instrumentStubsPath/$it" }
            .forEach(instrumentServiceMocker::addMock)
    }

    init {
        bootstrapInstrumentServiceMocker()
    }

    fun prepareKZOSCreateTopSell(): InstrumentId {
        val iid = instrumentServiceMocker.getIid("kzos")
        offerBookServiceMock.addMock(
            path = "stub/price/offer/kzos.json",
            iid = iid
        )
        return iid
    }

    fun prepareKZOSCreateFractionalSpreadBuy(): InstrumentId {
        val iid = instrumentServiceMocker.getIid("kzos")
        offerBookServiceMock.addMock(
            path = "stub/price/offer/kzos.json",
            iid = iid
        )
        return iid
    }
}
