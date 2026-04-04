package ru.pashkovske.buratino.assignment.service.core.cancel

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

@Service
class ContinuousFractionalSpreadAssignmentCanceller(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator,
    childAssignmentCanceller: FractionalSpreadAssignmentCanceller
) : ContinuousAssignmentCanceller<FractionalSpreadAssignment, ContinuousFractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    continueNotifyOrchestrator = continueNotifyOrchestrator,
    childAssignmentCanceller = childAssignmentCanceller,
)