package ru.pashkovske.buratino.assignment.parent.base.service.start

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.BasicAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

abstract class ParentAssignmentStarter<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ParentA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ParentA>,
    protected val childAssignmentExe: AssignmentExe<ChildA>
) : BasicAssignmentStarter<ParentA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doStart(ctx: ExeCtx<ParentA>) {
        checkAndStartChild(ctx)
    }

    protected fun checkAndStartChild(ctx: ExeCtx<ParentA>) {
        val assignment: ParentA = ctx.assignment
        if (assignment.child.state != AssignmentState.QUEUED) {
            log.warn("Child assignment ${assignment.child.id} is in ${assignment.child.state} state, skipping start")
            return
        }
        log.info("Starting child assignment: ${assignment.child.id}")
        assignment.child = childAssignmentExe.start(assignment.child)
        ctx.setMutated()
    }
}
