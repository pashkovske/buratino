package ru.pashkovske.buratino.assignment.spread.iteration.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.service.LimitOrderAssignmentExecutor
import ru.pashkovske.buratino.assignment.spread.iteration.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice

@Service
class FractionalSpreadAssignmentExecutor(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<FractionalSpreadAssignment>
): LimitOrderAssignmentExecutor<FractionalSpreadAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo
) {
    override fun getPrice(assignment: FractionalSpreadAssignment): MoneyPrice {
        TODO("Not yet implemented")
    }
}
