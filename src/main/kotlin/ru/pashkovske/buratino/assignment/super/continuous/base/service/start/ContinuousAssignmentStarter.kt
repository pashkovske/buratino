package ru.pashkovske.buratino.assignment.`super`.continuous.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.`super`.base.service.start.SuperAssignmentStarter
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class ContinuousAssignmentStarter<
    ContinuousA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousA>,
    nestedAssignmentExe: AssignmentExe<Nested>,
    private val continuousTaskScheduler: TaskScheduler,
    private val continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousA>
) : SuperAssignmentStarter<ContinuousA, Nested>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    nestedAssignmentExe = nestedAssignmentExe
) {

    override fun postStart(ctx: ExeCtx<ContinuousA>) {
        scheduleContinue(ctx)
        super.postStart(ctx)
    }

    private fun scheduleContinue(ctx: ExeCtx<ContinuousA>) {
        val assignment: ContinuousA = ctx.assignment
        val scheduled: Boolean = scheduleContinue(assignment)
        if (scheduled) {
            ctx.setMutated()
        }
    }

    private fun scheduleContinue(assignment: ContinuousA): Boolean {
        val schedulingProps: SchedulingProperties = assignment.continueSchedulingProperties ?: return false

        val subscriber = AssignmentSchedulingSubscriber(
            action = continuousAssignmentContinuer::continueAssignment,
            assignmentId = assignment.id
        )
        val taskId: UUID = continuousTaskScheduler.startNewPeriodic(
            period = schedulingProps.interval,
            subscriber = subscriber
        )
        val assignmentScheduling = AssignmentScheduling(
            properties = schedulingProps,
            taskId = taskId
        )
        assignmentScheduling.state = SchedulingState.ACTIVE
        assignment.initContinueScheduling(assignmentScheduling)
        return true
    }
}
