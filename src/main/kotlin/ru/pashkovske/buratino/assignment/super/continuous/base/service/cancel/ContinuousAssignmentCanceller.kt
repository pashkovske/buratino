package ru.pashkovske.buratino.assignment.`super`.continuous.base.service.cancel

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.`super`.base.service.cancel.SuperAssignmentCanceller
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

abstract class ContinuousAssignmentCanceller<
    NestedA : Assignment,
    ContinuousA : ContinuousAssignment<NestedA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    taskScheduler: TaskScheduler,
    nestedAssignmentExe: AssignmentExe<NestedA>,
    private val continuousTaskScheduler: TaskScheduler
) : SuperAssignmentCanceller<ContinuousA, NestedA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    nestedAssignmentExe = nestedAssignmentExe
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
