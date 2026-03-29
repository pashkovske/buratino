package ru.pashkovske.buratino.assignment.limit.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignmentStartCmd

@Service
final class TopPriceAssignmentExe(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<TopPriceAssignment>,
    assignmentRefresher: AssignmentRefresher<TopPriceAssignment>,
    assignmentCanceller: AssignmentCanceller<TopPriceAssignment>,
    assignmentStarter: AssignmentStarter<TopPriceAssignment>,
    assignmentBuilder: AssignmentBuilder<TopPriceAssignment, TopPriceAssignmentStartCmd>
) : LimitOrderAssignmentExe<
    TopPriceAssignment,
    TopPriceAssignmentStartCmd
    >(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)
