package ru.pashkovske.buratino.tinkoff.service.price.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "current_spreads")
data class CurrentSpread(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "instrument_uid", nullable = false)
    val instrumentUid: String,
    
    @Column(name = "min_price_units", nullable = false)
    val minPriceUnits: Long,
    
    @Column(name = "min_price_nanos", nullable = false)
    val minPriceNanos: Int,
    
    @Column(name = "max_price_units", nullable = false)
    val maxPriceUnits: Long,
    
    @Column(name = "max_price_nanos", nullable = false)
    val maxPriceNanos: Int,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val minPrice: Price
        get() = Price(minPriceUnits, minPriceNanos)
    
    val maxPrice: Price
        get() = Price(maxPriceUnits, maxPriceNanos)
    
    fun updatePrices(newMinPrice: Price, newMaxPrice: Price): CurrentSpread {
        return copy(
            minPriceUnits = newMinPrice.units,
            minPriceNanos = newMinPrice.nanos,
            maxPriceUnits = newMaxPrice.units,
            maxPriceNanos = newMaxPrice.nanos,
            updatedAt = LocalDateTime.now()
        )
    }
    
    companion object {
        fun create(instrumentUid: String, minPrice: Price, maxPrice: Price): CurrentSpread {
            return CurrentSpread(
                instrumentUid = instrumentUid,
                minPriceUnits = minPrice.units,
                minPriceNanos = minPrice.nanos,
                maxPriceUnits = maxPrice.units,
                maxPriceNanos = maxPrice.nanos
            )
        }
    }
}
