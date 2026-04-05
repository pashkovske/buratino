package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

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
        continueNotifyOrchestrator.startForAssignment(assignment.id)
    }
}