package ru.pashkovske.buratino.order.dao.postgre

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.Named
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderCommitResult
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.OrderRequest
import ru.pashkovske.buratino.order.model.OrderType
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Suppress("unused")
abstract class OrderMapper {
    
    @Mapping(target = "iid", source = "instrumentId", qualifiedByName = ["stringToInstrumentId"])
    @Mapping(target = "request", source = "orderRow", qualifiedByName = ["rowToOrderRequest"])
    @Mapping(target = "commitResult", source = "orderRow", qualifiedByName = ["rowToCommitResult"])
    @Mapping(target = "currentInfo", source = "orderRow", qualifiedByName = ["rowToCurrentInfo"])
    abstract fun toOrder(orderRow: OrderRow): Order
    
    @Mapping(target = "instrumentId", source = "order.iid.id")
    @Mapping(target = "requestType", source = "order", qualifiedByName = ["getRequestType"])
    @Mapping(target = "requestDirection", source = "order.request.direction")
    @Mapping(target = "requestLots", source = "order.request.lots")
    @Mapping(target = "requestIdempotencyToken", source = "order.request.idempotencyToken")
    @Mapping(target = "requestPriceUnit", source = "order", qualifiedByName = ["getRequestPriceUnit"])
    @Mapping(target = "requestPriceNano", source = "order", qualifiedByName = ["getRequestPriceNano"])
    @Mapping(target = "requestPriceCurrency", source = "order", qualifiedByName = ["getRequestPriceCurrency"])
    @Mapping(target = "commitCommissionUnit", source = "order.commitResult.commission.unit")
    @Mapping(target = "commitCommissionNano", source = "order.commitResult.commission.nano")
    @Mapping(target = "commitCommissionCurrency", source = "order.commitResult.commission.currency")
    @Mapping(target = "commitTime", source = "order.commitResult.time")
    @Mapping(target = "currentRemainingLots", source = "order.currentInfo.remainingLots")
    @Mapping(target = "currentState", source = "order.currentInfo.state")
    @Mapping(target = "lastUpdate", source = "order.currentInfo.time")
    abstract fun toOrderRow(order: Order): OrderRow
    
    @Named("stringToInstrumentId")
    fun stringToInstrumentId(instrumentId: String): InstrumentId {
        return InstrumentId(instrumentId)
    }
    
    @Named("rowToOrderRequest")
    fun rowToOrderRequest(orderRow: OrderRow): OrderRequest {
        return when (orderRow.requestType) {
            OrderType.LIMIT -> {
                val price = Price(
                    unit = orderRow.requestPriceUnit ?: 0,
                    nano = orderRow.requestPriceNano ?: 0,
                    currency = orderRow.requestPriceCurrency ?: Currency.UNKNOWN
                )
                LimitOrderRequest(
                    iid = InstrumentId(orderRow.instrumentId),
                    direction = orderRow.requestDirection,
                    lots = orderRow.requestLots,
                    idempotencyToken = orderRow.requestIdempotencyToken,
                    price = price
                )
            }
            OrderType.MARKET -> {
                throw UnsupportedOperationException("MARKET orders are not yet supported")
            }
        }
    }
    
    @Named("rowToCommitResult")
    fun rowToCommitResult(orderRow: OrderRow): OrderCommitResult {
        return OrderCommitResult(
            commission = Price(
                unit = orderRow.commitCommissionUnit,
                nano = orderRow.commitCommissionNano,
                currency = orderRow.commitCommissionCurrency
            ),
            time = orderRow.commitTime
        )
    }
    
    @Named("rowToCurrentInfo")
    fun rowToCurrentInfo(orderRow: OrderRow): OrderInstantInfo {
        return OrderInstantInfo(
            remainingLots = orderRow.currentRemainingLots,
            state = orderRow.currentState,
            time = orderRow.lastUpdate
        )
    }
    
    @Named("getRequestType")
    fun getRequestType(order: Order): OrderType {
        return when (order.request) {
            is LimitOrderRequest -> OrderType.LIMIT
            else -> OrderType.MARKET
        }
    }
    
    @Named("getRequestPriceUnit")
    fun getRequestPriceUnit(order: Order): Long? {
        return (order.request as? LimitOrderRequest)?.price?.unit
    }
    
    @Named("getRequestPriceNano")
    fun getRequestPriceNano(order: Order): Int? {
        return (order.request as? LimitOrderRequest)?.price?.nano
    }
    
    @Named("getRequestPriceCurrency")
    fun getRequestPriceCurrency(order: Order): Currency? {
        return (order.request as? LimitOrderRequest)?.price?.currency
    }
}
