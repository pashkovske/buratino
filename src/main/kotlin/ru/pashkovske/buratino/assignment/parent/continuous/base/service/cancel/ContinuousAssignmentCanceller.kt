package ru.pashkovske.buratino.assignment.parent.continuous.base.service.cancel

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.parent.base.service.cancel.ParentAssignmentCanceller
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

abstract class ContinuousAssignmentCanceller<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    taskScheduler: TaskScheduler,
    childAssignmentCanceller: AssignmentCanceller<ChildA>,
    private val continuousTaskScheduler: TaskScheduler
) : ParentAssignmentCanceller<ContinuousA, ChildA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    childAssignmentCanceller = childAssignmentCanceller
) {

    override fun postCancel(ctx: ExeCtx<ContinuousA>) {
        stopSchedulingContinuation(ctx)
        super.postCancel(ctx)
    }

    private fun stopSchedulingContinuation(ctx: ExeCtx<ContinuousA>) {
        val assignmentScheduling: AssignmentScheduling = ctx.assignment.getContinueAssignmentScheduling() ?: return

        continuousTaskScheduler.stopPeriodic(assignmentScheduling.taskId)
        assignmentScheduling.state = SchedulingState.COMPLETED

        ctx.setMutated()
    }
}
