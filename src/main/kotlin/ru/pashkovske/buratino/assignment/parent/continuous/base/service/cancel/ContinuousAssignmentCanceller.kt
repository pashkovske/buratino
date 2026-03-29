package ru.pashkovske.buratino.assignment.parent.continuous.base.service.cancel

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.base.service.cancel.ParentAssignmentCanceller
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import java.util.UUID

abstract class ContinuousAssignmentCanceller<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousA>,
    childAssignmentCanceller: AssignmentCanceller<ChildA>,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousA, ChildA>
) : ParentAssignmentCanceller<ContinuousA, ChildA>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    childAssignmentCanceller = childAssignmentCanceller
) {

    override fun postCancel(ctx: ExeCtx<ContinuousA>) {
        stopContinueNotifier(ctx)
        super.postCancel(ctx)
    }

    private fun stopContinueNotifier(ctx: ExeCtx<ContinuousA>) {
        val assignment: ContinuousA = ctx.assignment
        val stopped: List<UUID> = continueNotifyOrchestrator.stopForAssignment(assignment.id)
        if (stopped.isNotEmpty()) {
            assignment.clearContinueScheduling()
            assignment.initContinueScheduling(continueNotifyOrchestrator.get(stopped.first())!!)
            ctx.setMutated()
        }
    }
}
