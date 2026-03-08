package ru.pashkovske.buratino.assignment.`super`.base.service

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import java.util.UUID

abstract class BasicSuperAssignmentExe<
    SuperA : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<SuperA>,
    taskScheduler: TaskScheduler,
    protected val nestedAssignmentExe: AssignmentExe<Nested>,
    protected val nestedAssignmentDao: AssignmentDao<Nested>
) : BasicAssignmentExe<SuperA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun preRefresh(id: UUID): ExeCtx<SuperA> {
        val ctx: ExeCtx<SuperA> = super.preRefresh(id)
        syncNested(ctx)
        return ctx
    }

    override fun preCancel(id: UUID): ExeCtx<SuperA> {
        val ctx: ExeCtx<SuperA> = super.preCancel(id)
        syncNested(ctx)
        return ctx
    }

    protected fun syncNested(ctx: ExeCtx<SuperA>) {
        ctx.assignment.nested = nestedAssignmentDao.get(ctx.assignment.nested.id)
    }

    protected fun isNestedCompleted(ctx: ExeCtx<SuperA>): Boolean {
        return ctx.assignment.nested.status == AssignmentStatus.COMPLETED
    }

    protected fun checkAndStartNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        if (assignment.nested.status != AssignmentStatus.QUEUED) {
            log.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping start")
            return
        }
        log.info("Starting nested assignment: ${assignment.nested.id}")
        assignment.nested = nestedAssignmentExe.start(assignment.nested)
        ctx.setMutated()
    }

    protected fun refreshNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        assignment.nested = nestedAssignmentExe.refresh(assignment.nested.id)
        ctx.setMutated()
    }

    protected fun cancelNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        if (assignment.nested.status == AssignmentStatus.COMPLETED) {
            log.warn("Nested assignment ${assignment.nested.id} is already completed, skipping cancel nested")
            return
        }
        if (assignment.nested.status == AssignmentStatus.QUEUED) {
            log.warn("Nested assignment ${assignment.nested.id} is not started, skipping cancel nested")
            return
        }
        log.info("Canceling nested assignment: ${assignment.nested.id}")
        assignment.nested = nestedAssignmentExe.cancel(assignment.nested.id)
        ctx.setMutated()
    }
}
