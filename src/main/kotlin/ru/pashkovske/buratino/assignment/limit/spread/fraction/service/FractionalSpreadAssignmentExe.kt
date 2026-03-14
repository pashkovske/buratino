package ru.pashkovske.buratino.assignment.limit.spread.fraction.service

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.service.MarketPriceService

@Service
final class FractionalSpreadAssignmentExe(
    orderService: OrderService,
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<FractionalSpreadAssignment>,
    assignmentCanceller: AssignmentCanceller<FractionalSpreadAssignment>,
    val marketDataService: MarketPriceService,
    val instrumentService: InstrumentService
): LimitOrderAssignmentExe<FractionalSpreadAssignment>(
    orderService = orderService,
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun getPrice(assignment: FractionalSpreadAssignment): Price {
        val instrument: Instrument = instrumentService.get(assignment.iid)
        val step: Price = instrument.minPriceIncrement
        val directTopPrice: Price = marketDataService.getOneStepOverTopOfBook(
            iid = assignment.iid,
            direction = assignment.direction
        )!!
        val oppositeTopPrice: Price = marketDataService.getTopOfBook(
            iid = assignment.iid,
            direction = assignment.direction.getOpposite()
        )!!
        val askPrice: Price = if (assignment.direction == OrderDirection.BUY) {
            directTopPrice
        } else {
            oppositeTopPrice
        }
        val adjustedMinSpreadDelta: Price = step * (askPrice * assignment.rate / step)
        val topSpreadPrice: Price = if (assignment.direction == OrderDirection.BUY) {
            oppositeTopPrice - adjustedMinSpreadDelta
        } else {
            oppositeTopPrice + adjustedMinSpreadDelta
        }
        log.info { "topSpreadPrice: $topSpreadPrice" }
        log.info { "directTopPrice: $directTopPrice" }
        log.info { "adjustedMinSpreadDelta: $adjustedMinSpreadDelta" }
        return if (assignment.direction == OrderDirection.BUY) {
            minOf(topSpreadPrice, directTopPrice)
        } else {
            maxOf(topSpreadPrice, directTopPrice)
        }
    }
}
