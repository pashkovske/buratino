package ru.pashkovske.buratino.order.adapter.tinkoff

import ru.pashkovske.buratino.common.utils.TimeUtils
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderCommitResult
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.OrderRequest
import ru.pashkovske.buratino.order.model.OrderState
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.TinkoffPriceMapper
import java.time.Instant

object TinkoffOrderMapper {
    private val priceMapper = TinkoffPriceMapper

    fun map(orderDirection: OrderDirection): ru.tinkoff.piapi.contract.v1.OrderDirection {
        return when (orderDirection) {
            OrderDirection.BUY -> ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_BUY
            OrderDirection.SELL -> ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_SELL
        }
    }

    fun map(tinkoffOrderDirection: ru.tinkoff.piapi.contract.v1.OrderDirection): OrderDirection {
        return when (tinkoffOrderDirection) {
            ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_BUY -> OrderDirection.BUY
            ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_SELL -> OrderDirection.SELL
            else -> throw IllegalArgumentException("Unsupported offer direction $tinkoffOrderDirection")
        }
    }

    fun map(
        tinkoffOrderResponse: ru.tinkoff.piapi.contract.v1.PostOrderResponse,
        orderRequest: OrderRequest
    ): Order {
        return Order(
            id = tinkoffOrderResponse.orderId,
            iid = InstrumentId(tinkoffOrderResponse.instrumentUid),
            request = orderRequest,
            commitResult = OrderCommitResult(
                commission = priceMapper.map(tinkoffOrderResponse.initialCommission),
                time = TimeUtils.tsToInstant(tinkoffOrderResponse.responseMetadata.serverTime)
            ),
            currentInfo = map(tinkoffOrderResponse)
        )
    }

    fun map(tinkoffOrderResponse: ru.tinkoff.piapi.contract.v1.PostOrderResponse): OrderInstantInfo {
        return OrderInstantInfo(
            state = map(tinkoffOrderResponse.executionReportStatus),
            remainingLots = tinkoffOrderResponse.lotsRequested - tinkoffOrderResponse.lotsExecuted,
            time = TimeUtils.tsToInstant(tinkoffOrderResponse.responseMetadata.serverTime)
        )
    }

    fun map(
        tinkoffOrderState: ru.tinkoff.piapi.contract.v1.OrderState,
        time: Instant
    ): OrderInstantInfo {
        return OrderInstantInfo(
            state = map(tinkoffOrderState.executionReportStatus),
            remainingLots = tinkoffOrderState.lotsRequested - tinkoffOrderState.lotsExecuted,
            time = time
        )
    }

    fun map(tinkoffOrderExecutionStatus: ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus): OrderState {
        return when (tinkoffOrderExecutionStatus) {
            ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_NEW -> OrderState.ACTIVE
            ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_CANCELLED -> OrderState.COMPLETED
            ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_REJECTED -> OrderState.COMPLETED
            ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_PARTIALLYFILL -> OrderState.ACTIVE
            ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_FILL -> OrderState.COMPLETED
            else -> throw IllegalArgumentException("Unknown execution status: $tinkoffOrderExecutionStatus")
        }
    }
}
