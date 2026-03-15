package ru.pashkovske.buratino.assignment.base.service

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicAssignmentExe<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    >(
    protected val assignmentDao: AssignmentDao<A>,
    protected val taskScheduler: TaskScheduler,
    protected val assignmentRefresher: AssignmentRefresher<A>,
    protected val assignmentCanceller: AssignmentCanceller<A>,
    protected val assignmentStarter: AssignmentStarter<A>,
    protected val assignmentBuilder: AssignmentBuilder<A, Cmd>
) : AssignmentExe<A, Cmd> {

    @PostConstruct
    override fun recoverAssignments(): List<A> {
        val activeAssignments: List<A> = assignmentDao.getByState(AssignmentState.IN_PROGRESS)
        activeAssignments.filter { assignment: A ->
            assignment.refreshAssignmentSchedulingProperties != null
        }.forEach { assignment: A ->
            assignment.clearRefreshScheduling()
            scheduleRefresh(assignment)
            assignmentDao.update(assignment)
            refresh(assignment.id)
        }
        return activeAssignments
    }

    final override fun start(cmd: Cmd): A {
        val assignment: A = assignmentBuilder.build(cmd)
        return assignmentStarter.start(assignment)
    }

    final override fun refresh(id: UUID): A {
        return assignmentRefresher.refresh(id)
    }

    final override fun cancel(id: UUID): A {
        return assignmentCanceller.cancel(id)
    }

    private fun scheduleRefresh(assignment: A) {
        val schedulingProps: AssignmentSchedulingProperties = assignment.refreshAssignmentSchedulingProperties ?: return
        if (schedulingProps !is PeriodicAssignmentSchedulingProperties) {
            return
        }

        val subscriber = AssignmentSchedulingSubscriber(
            action = this::refresh,
            assignmentId = assignment.id
        )
        val taskId: UUID = taskScheduler.startNewPeriodic(
            period = schedulingProps.period,
            subscriber = subscriber
        )
        val assignmentScheduling = AssignmentScheduling(
            properties = schedulingProps,
            taskId = taskId,
            state = SchedulingState.ACTIVE
        )
        assignmentScheduling.state = SchedulingState.ACTIVE
        assignment.initRefreshScheduling(assignmentScheduling)
    }
}
