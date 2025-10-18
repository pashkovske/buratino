package ru.pashkovske.buratino.assignment.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.service.AssignmentExecutor
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.money.service.MarketMoneyPriceService
import java.util.UUID

private val logger = mu.KotlinLogging.logger {}

@Service
class TopPriceAssignmentExecutor(
    val marketDataService: MarketMoneyPriceService,
    val orderService: OrderService,
    val instrumentService: InstrumentService,
    val assignmentRepo: AssignmentRepo<TopPriceAssignment>
) : AssignmentExecutor<TopPriceAssignment> {
    override fun start(assignment: TopPriceAssignment): TopPriceAssignment {
        logger.info("Starting assignment: $assignment")
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.orderId = order.id
        assignment.status = AssignmentStatus.IN_PROGRESS
        assignmentRepo.create(assignment)
        return assignment
    }

    override fun refresh(id: UUID): TopPriceAssignment {
        logger.info("Refreshing assignment: $id")
        val assignment = assignmentRepo.get(id)
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping refresh")
            return assignment
        }
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            logger.info("Order of assignment ${assignment.id} is already completed, skipping refresh")
            assignment.status = AssignmentStatus.COMPLETED
        }
        else {
            val newOrder: Order = orderService.replaceOrder(
                orderId = orderId,
                newOrderRequest = buildLimitReq(assignment)
            )
            logger.info("Refreshed order: $orderId")
            assignment.status = AssignmentStatus.IN_PROGRESS
            assignment.info.orderId = newOrder.id
        }
        assignmentRepo.update(assignment)
        return assignment
    }

    override fun cancel(id: UUID): TopPriceAssignment {
        logger.info("Cancelling assignment: $id")
        val assignment = assignmentRepo.get(id)
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping cancel")
            return assignment
        }
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            logger.info("Order of assignment ${assignment.id} is already completed, skipping cancel")
        }
        else {
            orderService.cancelOrder(orderId)
            logger.info("Canceled order: $orderId")
        }
        assignment.status = AssignmentStatus.COMPLETED
        assignmentRepo.update(assignment)
        return assignment
    }

    private fun getTopPrice(assignment: TopPriceAssignment): MoneyPrice {
        val instrument: Instrument = instrumentService.get(assignment.iid)
        var moneyTopPrice: MoneyPrice = marketDataService.getTopOfBook(
            iid = assignment.iid,
            direction = assignment.direction
        )!!
        if (assignment.oneStepOver) {
            if (assignment.direction == OrderDirection.BUY) {
                moneyTopPrice += instrument.minPriceIncrement
            } else {
                moneyTopPrice -= instrument.minPriceIncrement
            }
        }
        return moneyTopPrice
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

    private fun getOrderId(assignment: TopPriceAssignment): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
