package ru.pashkovske.buratino.assignment.limit.top.price.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.base.service.start.LimitOrderAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.service.MarketPriceService

@Service
class TopPriceAssignmentStarter(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<TopPriceAssignment>,
    orderService: OrderService,
    private val marketDataService: MarketPriceService
) : LimitOrderAssignmentStarter<TopPriceAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    orderService = orderService
) {

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
}
