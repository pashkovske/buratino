package ru.pashkovske.buratino.assignment.service.core.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

@Service
class ContinuousFractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator,
    private val childAssignmentStarter: AssignmentStarter<FractionalSpreadAssignment>
) : ContinuousAssignmentStarter<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    continueNotifyOrchestrator = continueNotifyOrchestrator
) {

    override fun doStart(ctx: ExeCtx<ContinuousFractionalSpreadAssignment>) {
        childAssignmentStarter.start(ctx.assignment.child)
    }
}