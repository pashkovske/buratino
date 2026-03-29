package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.cancel

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.cancel.FractionalSpreadAssignmentCanceller
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.cancel.ContinuousAssignmentCanceller
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentCanceller(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousFractionalSpreadAssignment>,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>,
    childAssignmentCanceller: FractionalSpreadAssignmentCanceller
) : ContinuousAssignmentCanceller<FractionalSpreadAssignment, ContinuousFractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    continueNotifyOrchestrator = continueNotifyOrchestrator,
    childAssignmentCanceller = childAssignmentCanceller,
)
