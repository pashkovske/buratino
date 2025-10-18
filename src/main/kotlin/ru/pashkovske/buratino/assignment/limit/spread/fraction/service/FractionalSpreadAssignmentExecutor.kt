package ru.pashkovske.buratino.assignment.limit.spread.fraction.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExecutor
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.money.service.MarketMoneyPriceService

private val logger = KotlinLogging.logger {}

@Service
class FractionalSpreadAssignmentExecutor(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<FractionalSpreadAssignment>,
    val marketDataService: MarketMoneyPriceService,
    val instrumentService: InstrumentService
): LimitOrderAssignmentExecutor<FractionalSpreadAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo
) {
    override fun getPrice(assignment: FractionalSpreadAssignment): MoneyPrice {
        val instrument: Instrument = instrumentService.get(assignment.iid)
        val step: MoneyPrice = instrument.minPriceIncrement
        val directTopPrice: MoneyPrice = marketDataService.getOneStepOverTopOfBook(
            iid = assignment.iid,
            direction = assignment.direction
        )!!
        val oppositeTopPrice: MoneyPrice = marketDataService.getTopOfBook(
            iid = assignment.iid,
            direction = assignment.direction.getOpposite()
        )!!
        val askPrice: MoneyPrice = if (assignment.direction == OrderDirection.BUY) {
            directTopPrice
        } else {
            oppositeTopPrice
        }
        val adjustedMinSpreadDelta: MoneyPrice = step * (askPrice * assignment.rate / step).toInt()
        val topSpreadPrice: MoneyPrice = if (assignment.direction == OrderDirection.BUY) {
            oppositeTopPrice - adjustedMinSpreadDelta
        } else {
            oppositeTopPrice + adjustedMinSpreadDelta
        }
        logger.info { "topSpreadPrice: $topSpreadPrice" }
        logger.info { "directTopPrice: $directTopPrice" }
        logger.info { "adjustedMinSpreadDelta: $adjustedMinSpreadDelta" }
        return if (assignment.direction == OrderDirection.BUY) {
            minOf(topSpreadPrice, directTopPrice)
        } else {
            maxOf(topSpreadPrice, directTopPrice)
        }
    }
}
