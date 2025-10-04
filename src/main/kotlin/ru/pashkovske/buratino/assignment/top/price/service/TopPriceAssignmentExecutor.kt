package ru.pashkovske.buratino.assignment.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.service.AssignmentExecutor
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.price.model.MoneyPrice
import ru.pashkovske.buratino.price.price.model.Quotation
import ru.pashkovske.buratino.price.price.service.MarketPriceService
import java.util.UUID

@Service
class TopPriceAssignmentExecutor(
    val marketDataService: MarketPriceService,
    val orderService: OrderService,
    val instrumentService: InstrumentService,
    val assignmentRepo: AssignmentRepo<TopPriceAssignment>
) : AssignmentExecutor<TopPriceAssignment> {
    override fun run(assignment: TopPriceAssignment) {
        val instrument: Instrument = instrumentService.get(assignment.iid)
        val topBuyPrice: Quotation? = marketDataService.getTopOfBook(
            instrument = instrument,
            direction = assignment.direction
        )
        val limitOrderReq = LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = MoneyPrice(
                quotation = topBuyPrice!!,
                currency = instrument.currency
            )
        )
        val order: Order = orderService.createOrder(
            orderRequest = limitOrderReq
        )
        assignment.info.order = order
        assignment.status = AssignmentStatus.IN_PROGRESS
        assignmentRepo.create(assignment)
    }
}