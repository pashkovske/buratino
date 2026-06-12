package ru.pashkovske.buratino.assignment.service.core.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.LimitOrderFactory
import ru.pashkovske.buratino.order.service.OrderService

@Service
class FractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<FractionalSpreadAssignment>
) : LimitOrderAssignmentStarter<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)