package ru.pashkovske.buratino.assignment.`super`.base.service

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment

abstract class BasicSuperAssignmentExe<
    SuperA : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<SuperA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<SuperA>,
    assignmentCanceller: AssignmentCanceller<SuperA>,
    protected val nestedAssignmentExe: AssignmentExe<Nested>,
    protected val nestedAssignmentDao: AssignmentDao<Nested>
) : BasicAssignmentExe<SuperA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller
) {

    private val log: KLogger = KotlinLogging.logger {}

    protected fun syncNested(ctx: ExeCtx<SuperA>) {
        ctx.assignment.nested = nestedAssignmentDao.get(ctx.assignment.nested.id)
    }

    protected fun isNestedCompleted(ctx: ExeCtx<SuperA>): Boolean {
        return ctx.assignment.nested.state == AssignmentState.COMPLETED
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
