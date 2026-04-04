package ru.pashkovske.buratino.assignment.service.core.cancel

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import java.util.UUID

abstract class ContinuousAssignmentCanceller<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    childAssignmentCanceller: AssignmentCanceller<ChildA>,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator
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