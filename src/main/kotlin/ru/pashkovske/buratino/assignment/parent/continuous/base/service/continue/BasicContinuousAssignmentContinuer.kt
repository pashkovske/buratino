package ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import java.util.UUID

abstract class BasicContinuousAssignmentContinuer<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>
    >(
    private val assignmentDao: AssignmentDao<ContinuousA>,
    private val childAssignmentDao: AssignmentDao<ChildA>
) : ContinuousAssignmentContinuer<ContinuousA> {

    private val log: KLogger = KotlinLogging.logger {}

    final override fun continueAssignment(id: UUID): ContinuousA {
        val ctx: ExeCtx<ContinuousA> = preContinue(id)
        if (!ctx.shouldSkip()) {
            doContinue(ctx)
        }
        postContinue(ctx)
        return ctx.assignment
    }
    protected open fun preContinue(id: UUID): ExeCtx<ContinuousA> {
        val assignment: ContinuousA = assignmentDao.get(id)
        log.info("Continuing assignment: $assignment")
        val ctx: ExeCtx<ContinuousA> = ExeCtx(assignment)
        syncChild(ctx)
        if (isCompleted(ctx)) {
            log.warn("Assignment ${ctx.assignment.id} is already completed. Skipping continue")
            ctx.setShouldSkip()
        }
        return ctx
    }
    protected abstract fun doContinue(ctx: ExeCtx<ContinuousA>)
    protected open fun postContinue(ctx: ExeCtx<ContinuousA>) {
        if (ctx.isMutated()) {
            assignmentDao.update(ctx.assignment)
        }
        log.info("Assignment continued: ${ctx.assignment}")
    }

    protected fun syncChild(ctx: ExeCtx<ContinuousA>) {
        ctx.assignment.child = childAssignmentDao.get(ctx.assignment.child.id)
    }

    protected fun isCompleted(ctx: ExeCtx<ContinuousA>): Boolean {
        return ctx.assignment.state == AssignmentState.COMPLETED
    }

    protected fun isChildCompleted(ctx: ExeCtx<ContinuousA>): Boolean {
        return ctx.assignment.child.state == AssignmentState.COMPLETED
    }
}
