package ru.pashkovske.buratino.assignment.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.service.AssignmentExecutor
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderState
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.price.model.MoneyPrice
import ru.pashkovske.buratino.price.price.model.Quotation
import ru.pashkovske.buratino.price.price.service.MarketPriceService
import java.util.UUID

private val logger = mu.KotlinLogging.logger {}

@Service
class TopPriceAssignmentExecutor(
    val marketDataService: MarketPriceService,
    val orderService: OrderService,
    val instrumentService: InstrumentService,
    val assignmentRepo: AssignmentRepo<TopPriceAssignment>
) : AssignmentExecutor<TopPriceAssignment> {
    override fun start(assignment: TopPriceAssignment) {
        logger.info("Starting assignment: $assignment")
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.order = order
        assignment.status = AssignmentStatus.IN_PROGRESS
        assignmentRepo.create(assignment)
    }

    override fun refresh(id: UUID) {
        logger.info("Refreshing assignment: $id")
        val assignment = assignmentRepo.get(id)
        val order = getOrder(assignment)
        orderService.refreshOrder(order)
        if (order.currentInfo.state == OrderState.COMPLETED) {
            assignment.status = AssignmentStatus.COMPLETED
        }
        else {
            logger.info("Refreshed order: $order")
            val order: Order = orderService.replaceOrder(
                order = order,
                newOrderRequest = buildLimitReq(assignment)
            )
            assignment.info.order = order
            assignment.status = AssignmentStatus.IN_PROGRESS
        }
        assignmentRepo.update(assignment)
    }

    override fun cancel(id: UUID) {
        logger.info("Cancelling assignment: $id")
        val assignment = assignmentRepo.get(id)
        val order = getOrder(assignment)
        orderService.refreshOrder(order)
        if (order.currentInfo.state != OrderState.COMPLETED) {
            orderService.cancelOrder(order)
            logger.info("Canceled order: $order")
            orderService.refreshOrder(order)
        }
        assignment.status = AssignmentStatus.COMPLETED
        assignmentRepo.update(assignment)
    }

    private fun getTopPrice(assignment: TopPriceAssignment): MoneyPrice {
        val instrument: Instrument = instrumentService.get(assignment.iid)
        val topBuyPrice: Quotation? = marketDataService.getTopOfBook(
            instrument = instrument,
            direction = assignment.direction
        )
        return MoneyPrice(
            quotation = topBuyPrice!!,
            currency = instrument.currency
        )
    }

    private fun buildLimitReq(assignment: TopPriceAssignment): LimitOrderRequest {
        return LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = getTopPrice(assignment)
        )
    }

    private fun getOrder(assignment: TopPriceAssignment): Order {
        return assignment.info.order
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
