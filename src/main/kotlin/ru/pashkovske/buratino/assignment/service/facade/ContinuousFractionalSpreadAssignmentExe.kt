package ru.pashkovske.buratino.assignment.service.facade

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.core.build.ContinuousFractionalSpreadAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter

@Service
final class ContinuousFractionalSpreadAssignmentExe(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator,
    assignmentRefresher: AssignmentRefresher<ContinuousFractionalSpreadAssignment>,
    assignmentCanceller: AssignmentCanceller<ContinuousFractionalSpreadAssignment>,
    assignmentStarter: AssignmentStarter<ContinuousFractionalSpreadAssignment>,
    assignmentBuilder: ContinuousFractionalSpreadAssignmentBuilder,
    childAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousFractionalSpreadAssignment>
) : BasicContinuousAssignmentExe<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentStartCmd,
    FractionalSpreadAssignmentStartCmd
    >(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    continueNotifyOrchestrator = continueNotifyOrchestrator,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder,
    childAssignmentDao = childAssignmentDao,
    continuousAssignmentContinuer = continuousAssignmentContinuer
)