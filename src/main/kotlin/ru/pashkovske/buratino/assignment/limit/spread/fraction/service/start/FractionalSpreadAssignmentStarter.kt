package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.base.service.start.LimitOrderAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.order.service.OrderService

@Service
class FractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<FractionalSpreadAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<FractionalSpreadAssignment>
) : LimitOrderAssignmentStarter<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
