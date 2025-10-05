package ru.pashkovske.buratino.instrument.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService

@Suppress("unused")
@RestController
@RequestMapping("/instrument")
class InstrumentController(
    private val instrumentService: InstrumentService
) {

    @GetMapping("/any/{ticker}")
    fun getByTicker(@PathVariable ticker: String): Instrument {
        return instrumentService.getByTicker(ticker)
    }
}
