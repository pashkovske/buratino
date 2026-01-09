package ru.pashkovske.buratino.assignment.limit.spread.fraction.exe

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.limit.base.executor.LimitOrderAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.service.MarketPriceService
import java.util.UUID

@Service
final class FractionalSpreadAssignmentExe(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<FractionalSpreadAssignment>,
    assignmentScheduler: AssignmentTaskScheduler,
    val marketDataService: MarketPriceService,
    val instrumentService: InstrumentService
): LimitOrderAssignmentExe<FractionalSpreadAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
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

    override fun start(assignment: FractionalSpreadAssignment): FractionalSpreadAssignment {
        logStart(assignment)
        startLimitOrder(assignment)
        scheduleRefresh(assignment)
        setStatusInProgress(assignment)
        createInRepo(assignment)
        return assignment
    }

    override fun refresh(id: UUID): FractionalSpreadAssignment {
        val assignment: FractionalSpreadAssignment = getFromRepo(id)
        logRefresh(assignment)
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment $id is already completed. Skipping refresh")
            return assignment
        }
        refreshLimitOrder(assignment)
        setStatusInProgress(assignment)
        updateInRepo(assignment)
        return assignment
    }

    override fun cancel(id: UUID): FractionalSpreadAssignment {
        val assignment: FractionalSpreadAssignment = getFromRepo(id)
        logCancel(assignment)
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment $id is already completed. Skipping cancel")
            return assignment
        }
        stopSchedulingRefresh(assignment)
        cancelLimitOrder(assignment)
        setStatusCompleted(assignment)
        updateInRepo(assignment)
        return assignment
    }
}
