package ru.pashkovske.buratino.assignment.`super`.base.service.start

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.BasicAssignmentStarter
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

abstract class SuperAssignmentStarter<
    SuperA : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<SuperA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<SuperA>,
    protected val nestedAssignmentExe: AssignmentExe<Nested>
) : BasicAssignmentStarter<SuperA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doStart(ctx: ExeCtx<SuperA>) {
        checkAndStartNested(ctx)
    }

    protected fun checkAndStartNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        if (assignment.nested.state != AssignmentState.QUEUED) {
            log.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.state} state, skipping start")
            return
        }
        log.info("Starting nested assignment: ${assignment.nested.id}")
        assignment.nested = nestedAssignmentExe.start(assignment.nested)
        ctx.setMutated()
    }
}
