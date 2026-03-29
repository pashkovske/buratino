package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.start.ContinuousAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousFractionalSpreadAssignment>,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>,
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
