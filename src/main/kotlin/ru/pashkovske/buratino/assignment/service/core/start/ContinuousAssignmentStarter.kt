package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import java.util.UUID

abstract class ContinuousAssignmentStarter<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator
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