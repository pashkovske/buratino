package ru.pashkovske.buratino.assignment.limit.top.price.service

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.service.MarketPriceService
import java.util.UUID

@Service
final class TopPriceAssignmentExe(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<TopPriceAssignment>,
    val marketDataService: MarketPriceService,
    assignmentScheduler: AssignmentTaskScheduler
) : LimitOrderAssignmentExe<TopPriceAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun getPrice(assignment: TopPriceAssignment): Price {
        return if (assignment.oneStepOver) {
            marketDataService.getOneStepOverTopOfBook(
                iid = assignment.iid,
                direction = assignment.direction
            )!!
        } else {
            marketDataService.getTopOfBook(
                iid = assignment.iid,
                direction = assignment.direction
            )!!
        }
    }

    override fun start(assignment: TopPriceAssignment): TopPriceAssignment {
        logStart(assignment)
        startLimitOrder(assignment)
        scheduleRefresh(assignment)
        setStatusInProgress(assignment)
        createInRepo(assignment)
        return assignment
    }

    override fun refresh(id: UUID): TopPriceAssignment {
        val assignment: TopPriceAssignment = getFromRepo(id)
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

    override fun cancel(id: UUID): TopPriceAssignment {
        val assignment: TopPriceAssignment = getFromRepo(id)
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
