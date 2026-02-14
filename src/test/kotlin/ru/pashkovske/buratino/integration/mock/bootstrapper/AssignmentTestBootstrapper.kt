package ru.pashkovske.buratino.integration.mock.bootstrapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Share
import ru.pashkovske.buratino.integration.mock.InstrumentServiceMocker
import ru.pashkovske.buratino.integration.mock.order.OfferBookMock
import ru.pashkovske.buratino.util.loader.FileLoader

@Service
class AssignmentTestBootstrapper(
    private val instrumentServiceMocker: InstrumentServiceMocker,
    private val offerBookServiceMock: OfferBookMock
) {
    fun bootstrapInstrumentServiceMocker() {
        val shareStubsPath = "stub/instrument/share"
        val shareStubPaths: Set<String> = FileLoader.walkPath(shareStubsPath)
        shareStubPaths
            .map { "$shareStubsPath/$it" }
            .forEach {
                instrumentServiceMocker.addMock(it, Share::class.java)
            }
        val futureStubsPath = "stub/instrument/future"
        val futureStubsPaths: Set<String> = FileLoader.walkPath(futureStubsPath)
        futureStubsPaths
            .map { "$futureStubsPath/$it" }
            .forEach {
                instrumentServiceMocker.addMock(it, Future::class.java)
            }
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
