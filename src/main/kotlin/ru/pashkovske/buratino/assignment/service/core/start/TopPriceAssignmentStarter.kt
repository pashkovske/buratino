package ru.pashkovske.buratino.assignment.service.core.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.order.service.OrderService

@Service
class TopPriceAssignmentStarter(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<TopPriceAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<TopPriceAssignment>
) : LimitOrderAssignmentStarter<TopPriceAssignment>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
