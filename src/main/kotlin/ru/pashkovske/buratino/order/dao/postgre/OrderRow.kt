package ru.pashkovske.buratino.order.dao.postgre

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.OrderState
import ru.pashkovske.buratino.order.model.OrderType
import ru.pashkovske.buratino.price.model.Currency
import java.time.Instant
import java.util.UUID

@Table("order_table")
data class OrderRow(
    @Id 
    val id: String,
    
    // InstrumentId
    val instrumentId: String,
    
    // OrderRequest fields
    val requestType: OrderType,
    val requestDirection: OrderDirection,
    val requestLots: Long,
    val requestIdempotencyToken: UUID?,
    val requestPriceUnit: Long?,
    val requestPriceNano: Int?,
    val requestPriceCurrency: Currency?,
    
    // OrderCommitResult fields
    val commitCommissionUnit: Long,
    val commitCommissionNano: Int,
    val commitCommissionCurrency: Currency,
    val commitTime: Instant,
    
    // OrderInstantInfo fields
    val currentRemainingLots: Long,
    val currentState: OrderState,
    val lastUpdate: Instant
)
