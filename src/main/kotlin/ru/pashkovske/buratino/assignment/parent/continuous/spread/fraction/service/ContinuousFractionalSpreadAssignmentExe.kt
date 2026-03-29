package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.build.ContinuousFractionalSpreadAssignmentBuilder

@Service
final class ContinuousFractionalSpreadAssignmentExe(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousFractionalSpreadAssignment>,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>,
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
