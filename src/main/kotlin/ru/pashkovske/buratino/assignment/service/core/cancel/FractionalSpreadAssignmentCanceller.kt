package ru.pashkovske.buratino.assignment.service.core.cancel

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.order.service.OrderService

@Service
class FractionalSpreadAssignmentCanceller(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<FractionalSpreadAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<FractionalSpreadAssignment>
) : LimitOrderAssignmentCanceller<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)