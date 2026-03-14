package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.refresh

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.base.service.refresh.LimitOrderAssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.order.service.OrderService

@Service
class FractionalSpreadAssignmentRefresher(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<FractionalSpreadAssignment>
) : LimitOrderAssignmentRefresher<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
