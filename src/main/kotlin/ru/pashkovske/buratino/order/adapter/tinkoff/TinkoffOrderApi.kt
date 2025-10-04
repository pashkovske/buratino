package ru.pashkovske.buratino.order.adapter.tinkoff

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.TinkoffPriceMapper
import ru.pashkovske.buratino.price.price.model.Quotation
import ru.tinkoff.piapi.contract.v1.PostOrderResponse
import ru.tinkoff.piapi.contract.v1.PriceType
import ru.tinkoff.piapi.contract.v1.TimeInForceType
import ru.tinkoff.piapi.core.OrdersService
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
class TinkoffOrderApi(
       private val tinkoffOrderService: OrdersService,
       private val account: Account
): OrderService {
    private val priceMapper = TinkoffPriceMapper
    private val orderMapper = TinkoffOrderMapper

    override fun createOrder(orderRequest: LimitOrderRequest): Order {
        val response: PostOrderResponse = tinkoffOrderService.postLimitOrderSync(
            orderRequest.iid.id,
            orderRequest.lots,
            priceMapper.map(orderRequest.price as Quotation),
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
        order: Order,
        newOrderRequest: LimitOrderRequest
    ): Order {
        val response = tinkoffOrderService.replaceOrderSync(
            newOrderRequest.iid.id,
            newOrderRequest.lots,
            priceMapper.map(newOrderRequest.price as Quotation),
            newOrderRequest.idempotencyToken?.toString() ?: UUID.randomUUID().toString(),
            order.id,
            PriceType.PRICE_TYPE_CURRENCY
        )
        logger.info(
            """
                Replaced order:
                    account = ${account.name};
                    iid = ${response.instrumentUid};
                    old_order_id = ${order.id};
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

    override fun cancelOrder(order: Order) {
        tinkoffOrderService.cancelOrderSync(
            account.id,
            order.id
        )
        logger.info(
            """
                Canceled order:
                    account = ${account.name};
                    iid = ${order.iid.id};
                    order_id = ${order.id}
            """.trimIndent()
        )
    }
}