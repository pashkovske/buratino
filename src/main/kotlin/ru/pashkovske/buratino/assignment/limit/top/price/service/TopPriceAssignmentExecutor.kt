package ru.pashkovske.buratino.assignment.limit.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExecutor
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.money.service.MarketMoneyPriceService

@Service
class TopPriceAssignmentExecutor(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<TopPriceAssignment>,
    val marketDataService: MarketMoneyPriceService
) : LimitOrderAssignmentExecutor<TopPriceAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo
) {
    override fun getPrice(assignment: TopPriceAssignment): MoneyPrice {
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
