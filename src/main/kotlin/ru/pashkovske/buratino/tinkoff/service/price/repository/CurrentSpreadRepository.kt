package ru.pashkovske.buratino.tinkoff.service.price.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.tinkoff.service.price.model.CurrentSpread

@Repository
interface CurrentSpreadRepository : JpaRepository<CurrentSpread, Long> {
    
    fun findByInstrumentUid(instrumentUid: String): CurrentSpread?
    
    fun findByInstrumentUidIn(instrumentUids: List<String>): List<CurrentSpread>
    
    @Query("SELECT cs FROM CurrentSpread cs WHERE cs.minPrice.units >= :minPriceUnits AND cs.maxPrice.units <= :maxPriceUnits")
    fun findByPriceRange(
        @Param("minPriceUnits") minPriceUnits: Long,
        @Param("maxPriceUnits") maxPriceUnits: Long
    ): List<CurrentSpread>
    
    fun existsByInstrumentUid(instrumentUid: String): Boolean
}
