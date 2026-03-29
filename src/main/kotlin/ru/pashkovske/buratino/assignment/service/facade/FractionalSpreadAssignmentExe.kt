package ru.pashkovske.buratino.assignment.service.facade

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.core.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter

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