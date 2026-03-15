package ru.pashkovske.buratino.assignment.parent.continuous.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.base.service.start.ParentAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class ContinuousAssignmentStarter<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousA>,
    private val continuousTaskScheduler: TaskScheduler,
    private val continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousA>
) : ParentAssignmentStarter<ContinuousA, ChildA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher
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
        val schedulingProps: AssignmentSchedulingProperties = assignment.continueAssignmentSchedulingProperties ?: return false
        if (schedulingProps !is PeriodicAssignmentSchedulingProperties) {
            return false
        }

        val subscriber = AssignmentSchedulingSubscriber(
            action = continuousAssignmentContinuer::continueAssignment,
            assignmentId = assignment.id
        )
        val taskId: UUID = continuousTaskScheduler.startNewPeriodic(
            period = schedulingProps.period,
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
