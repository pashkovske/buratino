package ru.pashkovske.buratino.assignment.parent.base.service.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.cancel.BasicAssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment

abstract class ParentAssignmentCanceller<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ParentA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ParentA>,
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
