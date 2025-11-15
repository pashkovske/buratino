package ru.pashkovske.buratino.order.adapter.tinkoff

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.TinkoffPriceMapper
import ru.tinkoff.piapi.contract.v1.OrderState
import ru.tinkoff.piapi.contract.v1.PostOrderResponse
import ru.tinkoff.piapi.contract.v1.PriceType
import ru.tinkoff.piapi.contract.v1.TimeInForceType
import ru.tinkoff.piapi.core.OrdersService
import java.time.Instant
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
class TinkoffOrderApi(
       private val tinkoffOrderService: OrdersService,
       private val account: Account
): ExtOrderServiceAdapter {
    private val priceMapper = TinkoffPriceMapper
    private val orderMapper = TinkoffOrderMapper

    override fun createOrder(orderRequest: LimitOrderRequest): Order {
        val response: PostOrderResponse = tinkoffOrderService.postLimitOrderSync(
            orderRequest.iid.id,
            orderRequest.lots,
            priceMapper.mapToQuotation(orderRequest.price),
            orderMapper.map(orderRequest.direction),
            account.id,
            TimeInForceType.TIME_IN_FORCE_DAY,
            PriceType.PRICE_TYPE_CURRENCY,
            orderRequest.idempotencyToken?.toString() ?: UUID.randomUUID().toString()
        )
        logger.info(
            """
                Posted order:
                    account = ${account.name};
                    iid = ${response.instrumentUid};
                    order_id = ${response.orderId};
                    price_units = ${response.initialOrderPrice.units};
                    price_nanos = ${response.initialSecurityPrice.nano};
            """.trimIndent()
        )
        return orderMapper.map(
            tinkoffOrderResponse = response,
            orderRequest = orderRequest
        )
    }

    override fun replaceOrder(
        orderId: String,
        newOrderRequest: LimitOrderRequest
    ): Order {
        val response = tinkoffOrderService.replaceOrderSync(
            account.id,
            newOrderRequest.lots,
            priceMapper.mapToQuotation(newOrderRequest.price),
            newOrderRequest.idempotencyToken?.toString() ?: UUID.randomUUID().toString(),
            orderId,
            PriceType.PRICE_TYPE_CURRENCY
        )
        logger.info(
            """
                Replaced order:
                    account = ${account.name};
                    iid = ${response.instrumentUid};
                    old_order_id = $orderId;
                    order_id = ${response.orderId};
                    price_units = ${response.initialOrderPrice.units};
                    price_nanos = ${response.initialSecurityPrice.nano};
            """.trimIndent()
        )
        return orderMapper.map(
            tinkoffOrderResponse = response,
            orderRequest = newOrderRequest
        )
    }

    override fun getOrderActualInfo(orderId: String): OrderInstantInfo {
        val tinkoffOrderState: OrderState = tinkoffOrderService.getOrderStateSync(
            account.id,
            orderId
        )
        return orderMapper.map(
            tinkoffOrderState = tinkoffOrderState,
            time = Instant.now()
        )
    }

    override fun cancelOrder(orderId: String) {
        tinkoffOrderService.cancelOrderSync(
            account.id,
            orderId
        )
        logger.info(
            """
                Canceled order:
                    account = ${account.name};
                    order_id = $orderId
            """.trimIndent()
        )
    }
}