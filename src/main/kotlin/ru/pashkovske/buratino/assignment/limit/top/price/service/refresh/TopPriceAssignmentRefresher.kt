package ru.pashkovske.buratino.assignment.limit.top.price.service.refresh

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.limit.base.service.refresh.LimitOrderAssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.service.MarketPriceService

@Service
class TopPriceAssignmentRefresher(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    orderService: OrderService,
    private val marketDataService: MarketPriceService
) : LimitOrderAssignmentRefresher<TopPriceAssignment>(
    assignmentDao = assignmentDao,
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
