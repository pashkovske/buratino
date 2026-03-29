package ru.pashkovske.buratino.assignment.limit.spread.fraction.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd

@Service
final class FractionalSpreadAssignmentExe(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<FractionalSpreadAssignment>,
    assignmentRefresher: AssignmentRefresher<FractionalSpreadAssignment>,
    assignmentCanceller: AssignmentCanceller<FractionalSpreadAssignment>,
    assignmentStarter: AssignmentStarter<FractionalSpreadAssignment>,
    assignmentBuilder: AssignmentBuilder<FractionalSpreadAssignment, FractionalSpreadAssignmentStartCmd>
) : LimitOrderAssignmentExe<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentStartCmd
    >(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)
