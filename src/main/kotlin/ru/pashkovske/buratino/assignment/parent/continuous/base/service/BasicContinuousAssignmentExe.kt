package ru.pashkovske.buratino.assignment.parent.continuous.base.service

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.parent.base.service.ParentAssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.build.ContinuousAssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import java.util.UUID

abstract class BasicContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousStartCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousA>,
    assignmentCanceller: AssignmentCanceller<ContinuousA>,
    assignmentStarter: AssignmentStarter<ContinuousA>,
    assignmentBuilder: ContinuousAssignmentBuilder<ContinuousA, ChildA, ContinuousStartCmd, ChildCmd>,
    childAssignmentDao: AssignmentDao<ChildA>,
    protected val continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousA>
):
    ParentAssignmentExe<ContinuousA, ChildA, ContinuousStartCmd, ChildCmd>(
        assignmentDao = assignmentDao,
        taskScheduler = taskScheduler,
        assignmentRefresher = assignmentRefresher,
        assignmentCanceller = assignmentCanceller,
        assignmentStarter = assignmentStarter,
        assignmentBuilder = assignmentBuilder,
        childAssignmentDao = childAssignmentDao
    ),
    ContinuousAssignmentExe<ContinuousA, ChildA, ContinuousStartCmd>
{

    @PostConstruct
    override fun recoverAssignments(): List<ContinuousA> {
        val assignmentsToRecover: List<ContinuousA> = super.recoverAssignments()

        assignmentsToRecover.filter { assignment: ContinuousA ->
            assignment.continueAssignmentSchedulingProperties != null
        }.forEach { assignment: ContinuousA ->
            assignment.clearContinueScheduling()
            scheduleContinue(assignment)
            assignmentDao.update(assignment)
            continueAssignment(assignment.id)
        }
        return assignmentsToRecover
    }

    final override fun continueAssignment(id: UUID): ContinuousA {
        return continuousAssignmentContinuer.continueAssignment(id)
    }

    private fun scheduleContinue(assignment: ContinuousA): Boolean {
        val schedulingProps: AssignmentSchedulingProperties = assignment.continueAssignmentSchedulingProperties ?: return false
        if (schedulingProps !is PeriodicAssignmentSchedulingProperties) {
            return false
        }

        val subscriber = AssignmentSchedulingSubscriber(
            action = this::continueAssignment,
            assignmentId = assignment.id
        )
        val taskId: UUID = taskScheduler.startNewPeriodic(
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
