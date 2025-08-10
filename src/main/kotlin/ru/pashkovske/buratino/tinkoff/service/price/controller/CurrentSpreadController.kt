package ru.pashkovske.buratino.tinkoff.service.price.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector
import ru.pashkovske.buratino.tinkoff.service.price.dto.SpreadRequestDto
import ru.pashkovske.buratino.tinkoff.service.price.model.CurrentSpread
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentSpreadService

@RestController
@RequestMapping("/api/spreads")
class CurrentSpreadController(
    private val currentSpreadService: CurrentSpreadService,
    private val instrumentSelector: InstrumentSelector
) {
    
    private val log = LoggerFactory.getLogger(CurrentSpreadController::class.java)
    
    @PostMapping
    fun saveSpread(@RequestBody request: SpreadRequestDto): ResponseEntity<CurrentSpread> {
        log.info("Received request to save spread for instrument: ${request.instrumentUid}")
        val spread = currentSpreadService.saveSpread(
            request.instrumentUid,
            request.minPrice,
            request.maxPrice
        )
        return ResponseEntity.ok(spread)
    }
    
    @PostMapping("/from-market/{instrumentUid}")
    fun saveSpreadFromMarketData(@PathVariable instrumentUid: String): ResponseEntity<CurrentSpread> {
        log.info("Received request to save spread from market data for instrument: $instrumentUid")
        
        val instrument = instrumentSelector.findByUid(instrumentUid)
            ?: return ResponseEntity.notFound().build()
        
        val spread = currentSpreadService.saveSpreadFromMarketData(instrument)
        return ResponseEntity.ok(spread)
    }
    
    @PostMapping("/update-all-from-market")
    fun updateAllSpreadsFromMarketData(): ResponseEntity<List<CurrentSpread>> {
        log.info("Received request to update all spreads from market data")
        
        val instruments = instrumentSelector.getAllInstruments()
        val spreads = currentSpreadService.updateAllSpreadsFromMarketData(instruments)
        return ResponseEntity.ok(spreads)
    }
    
    @GetMapping("/{instrumentUid}")
    fun getSpreadByInstrumentUid(@PathVariable instrumentUid: String): ResponseEntity<CurrentSpread> {
        log.info("Received request to get spread for instrument: $instrumentUid")
        return currentSpreadService.getSpreadByInstrumentUid(instrumentUid)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
    
    @GetMapping
    fun getAllSpreads(): ResponseEntity<List<CurrentSpread>> {
        log.info("Received request to get all spreads")
        val spreads = currentSpreadService.getAllSpreads()
        return ResponseEntity.ok(spreads)
    }
    
    @GetMapping("/search")
    fun getSpreadsByPriceRange(
        @RequestParam minPriceUnits: Long,
        @RequestParam maxPriceUnits: Long
    ): ResponseEntity<List<CurrentSpread>> {
        log.info("Received request to search spreads by price range: $minPriceUnits - $maxPriceUnits")
        val spreads = currentSpreadService.getSpreadsByPriceRange(minPriceUnits, maxPriceUnits)
        return ResponseEntity.ok(spreads)
    }
    
    @DeleteMapping("/{instrumentUid}")
    fun deleteSpreadByInstrumentUid(@PathVariable instrumentUid: String): ResponseEntity<Void> {
        log.info("Received request to delete spread for instrument: $instrumentUid")
        currentSpreadService.deleteSpreadByInstrumentUid(instrumentUid)
        return ResponseEntity.noContent().build()
    }
}
