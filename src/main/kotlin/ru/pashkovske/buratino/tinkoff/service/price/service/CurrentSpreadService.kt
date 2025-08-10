package ru.pashkovske.buratino.tinkoff.service.price.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper
import ru.pashkovske.buratino.tinkoff.service.price.model.CurrentSpread
import ru.pashkovske.buratino.tinkoff.service.price.model.Price
import ru.pashkovske.buratino.tinkoff.service.price.repository.CurrentSpreadRepository
import ru.tinkoff.piapi.contract.v1.OrderDirection

@Service
class CurrentSpreadService(
    private val currentSpreadRepository: CurrentSpreadRepository,
    private val marketPriceService: MarketPriceService
) {
    
    private val log = LoggerFactory.getLogger(CurrentSpreadService::class.java)
    
    @Transactional
    fun saveSpreadFromMarketData(instrument: InstrumentWrapper): CurrentSpread {
        log.info("Saving spread for instrument: ${instrument.uid}")
        
        val bestBuyPrice = marketPriceService.getBestPrice(instrument, OrderDirection.ORDER_DIRECTION_BUY)
        val bestSellPrice = marketPriceService.getBestPrice(instrument, OrderDirection.ORDER_DIRECTION_SELL)
        
        if (bestBuyPrice == null || bestSellPrice == null) {
            throw IllegalStateException("Не удалось получить цены для инструмента: ${instrument.uid}")
        }
        
        val minPrice = Price.fromQuotation(bestBuyPrice)
        val maxPrice = Price.fromQuotation(bestSellPrice)
        
        val existingSpread = currentSpreadRepository.findByInstrumentUid(instrument.uid)
        
        return if (existingSpread != null) {
            val updatedSpread = existingSpread.updatePrices(minPrice, maxPrice)
            currentSpreadRepository.save(updatedSpread)
        } else {
            val newSpread = CurrentSpread.create(instrument.uid, minPrice, maxPrice)
            currentSpreadRepository.save(newSpread)
        }
    }
    
    @Transactional
    fun saveSpread(instrumentUid: String, minPrice: Price, maxPrice: Price): CurrentSpread {
        log.info("Saving spread for instrument $instrumentUid: min=$minPrice, max=$maxPrice")
        
        val existingSpread = currentSpreadRepository.findByInstrumentUid(instrumentUid)
        
        return if (existingSpread != null) {
            val updatedSpread = existingSpread.updatePrices(minPrice, maxPrice)
            currentSpreadRepository.save(updatedSpread)
        } else {
            val newSpread = CurrentSpread.create(instrumentUid, minPrice, maxPrice)
            currentSpreadRepository.save(newSpread)
        }
    }
    
    @Transactional(readOnly = true)
    fun getSpreadByInstrumentUid(instrumentUid: String): CurrentSpread? {
        return currentSpreadRepository.findByInstrumentUid(instrumentUid)
    }
    
    @Transactional(readOnly = true)
    fun getAllSpreads(): List<CurrentSpread> {
        return currentSpreadRepository.findAll()
    }
    
    @Transactional(readOnly = true)
    fun getSpreadsByInstrumentUids(instrumentUids: List<String>): List<CurrentSpread> {
        return currentSpreadRepository.findByInstrumentUidIn(instrumentUids)
    }
    
    @Transactional(readOnly = true)
    fun getSpreadsByPriceRange(minPriceUnits: Long, maxPriceUnits: Long): List<CurrentSpread> {
        return currentSpreadRepository.findByPriceRange(minPriceUnits, maxPriceUnits)
    }
    
    @Transactional
    fun deleteSpreadByInstrumentUid(instrumentUid: String) {
        currentSpreadRepository.findByInstrumentUid(instrumentUid)?.let {
            currentSpreadRepository.delete(it)
        }
    }
    
    @Transactional
    fun updateAllSpreadsFromMarketData(instruments: List<InstrumentWrapper>): List<CurrentSpread> {
        log.info("Updating spreads for ${instruments.size} instruments from market data")
        
        return instruments.mapNotNull { instrument ->
            try {
                saveSpreadFromMarketData(instrument)
            } catch (e: Exception) {
                log.error("Failed to update spread for instrument ${instrument.uid}: ${e.message}", e)
                null
            }
        }
    }
}
