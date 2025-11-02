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

    fun bootstrapOfferBookServiceMock() {
        val offerBookStubsPath = "stub/price/offer"
        val offerBookStubPaths: Set<String> = FileLoader.walkPath(offerBookStubsPath)
        offerBookStubPaths.forEach { offerBookStubFile ->
            val alias = FileLoader.fileNameToAlias(offerBookStubFile)
            val iid: InstrumentId = instrumentServiceMocker.getIid(alias) ?: return@forEach
            offerBookServiceMock.addMock(
                path = "$offerBookStubsPath/$offerBookStubFile",
                iid = iid
            )
        }
    }

    init {
        bootstrapInstrumentServiceMocker()
        bootstrapOfferBookServiceMock()
    }

    fun getIid(alias: String): InstrumentId {
        return instrumentServiceMocker.getIid(alias) ?: throw IllegalArgumentException("No such alias: $alias")
    }
}
