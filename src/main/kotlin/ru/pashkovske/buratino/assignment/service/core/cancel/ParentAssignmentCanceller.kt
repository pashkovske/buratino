package ru.pashkovske.buratino.assignment.service.core.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

abstract class ParentAssignmentCanceller<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ParentA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    protected val childAssignmentCanceller: AssignmentCanceller<ChildA>
) : BasicAssignmentCanceller<ParentA>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doCancel(ctx: ExeCtx<ParentA>) {
        cancelChild(ctx)
    }

    protected fun cancelChild(ctx: ExeCtx<ParentA>) {
        val assignment: ParentA = ctx.assignment
        if (assignment.child.state == AssignmentState.COMPLETED) {
            log.warn("Child assignment ${assignment.child.id} is already completed, skipping cancel child")
            return
        }
        if (assignment.child.state == AssignmentState.QUEUED) {
            log.warn("Child assignment ${assignment.child.id} is not started, skipping cancel child")
            return
        }
        log.info("Canceling child assignment: ${assignment.child.id}")
        assignment.child = childAssignmentCanceller.cancel(assignment.child.id)
        ctx.setMutated()
    }
}