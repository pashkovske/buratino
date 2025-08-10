package ru.pashkovske.buratino.tinkoff.service.price.model

import ru.tinkoff.piapi.contract.v1.Quotation

data class Price(
    val units: Long,
    val nanos: Int
) {
    override fun toString(): String {
        return "$units.$nanos"
    }
    
    companion object {
        fun fromQuotation(quotation: Quotation): Price {
            return Price(quotation.units, quotation.nano)
        }
        
        fun toQuotation(price: Price): Quotation {
            return Quotation.newBuilder()
                .setUnits(price.units)
                .setNano(price.nanos)
                .build()
        }
    }
}
