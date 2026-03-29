package ru.pashkovske.buratino.assignment.parent.continuous.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.base.service.start.ParentAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import java.util.UUID

abstract class ContinuousAssignmentStarter<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousA>,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousA, ChildA>
) : ParentAssignmentStarter<ContinuousA, ChildA>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    override fun postStart(ctx: ExeCtx<ContinuousA>) {
        startContinueNotifier(ctx)
        super.postStart(ctx)
    }

    private fun startContinueNotifier(ctx: ExeCtx<ContinuousA>) {
        val assignment: ContinuousA = ctx.assignment
        val started: List<UUID> = continueNotifyOrchestrator.startForAssignment(assignment.id)
        if (started.isNotEmpty()) {
            assignment.clearContinueScheduling()
            assignment.initContinueScheduling(continueNotifyOrchestrator.get(started.first())!!)
            ctx.setMutated()
        }
    }
}
