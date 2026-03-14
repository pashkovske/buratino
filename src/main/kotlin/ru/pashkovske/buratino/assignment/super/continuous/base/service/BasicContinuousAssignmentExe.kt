package ru.pashkovske.buratino.assignment.`super`.continuous.base.service

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.`super`.base.service.BasicSuperAssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import java.util.UUID

abstract class BasicContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousA>,
    assignmentCanceller: AssignmentCanceller<ContinuousA>,
    assignmentStarter: AssignmentStarter<ContinuousA>,
    nestedAssignmentExe: AssignmentExe<Nested>,
    nestedAssignmentDao: AssignmentDao<Nested>,
    protected val continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousA>
):
    BasicSuperAssignmentExe<ContinuousA, Nested>(
        assignmentDao = assignmentDao,
        taskScheduler = taskScheduler,
        assignmentRefresher = assignmentRefresher,
        assignmentCanceller = assignmentCanceller,
        assignmentStarter = assignmentStarter,
        nestedAssignmentExe = nestedAssignmentExe,
        nestedAssignmentDao = nestedAssignmentDao
    ),
    ContinuousAssignmentExe<ContinuousA>
{

    @PostConstruct
    override fun recoverAssignments(): List<ContinuousA> {
        val assignmentsToRecover: List<ContinuousA> = super.recoverAssignments()

        assignmentsToRecover.filter { assignment: ContinuousA ->
            assignment.continueSchedulingProperties != null
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
        val schedulingProps: SchedulingProperties = assignment.continueSchedulingProperties ?: return false

        val subscriber = AssignmentSchedulingSubscriber(
            action = this::continueAssignment,
            assignmentId = assignment.id
        )
        val taskId: UUID = taskScheduler.startNewPeriodic(
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
