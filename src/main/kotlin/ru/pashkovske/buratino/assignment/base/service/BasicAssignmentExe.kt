package ru.pashkovske.buratino.assignment.base.service

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingAssignmentTask
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import java.util.UUID

abstract class BasicAssignmentExe<A: Assignment>(
    protected val assignmentRepo: AssignmentRepo<A>,
    protected val assignmentScheduler: AssignmentTaskScheduler
): AssignmentExe<A> {

    private val logger: KLogger = KotlinLogging.logger {}

    protected fun createInRepo(assignment: A) {
        assignmentRepo.create(assignment)
    }
    protected fun getFromRepo(id: UUID): A {
        return assignmentRepo.get(id)
    }
    protected fun updateInRepo(assignment: A) {
        assignmentRepo.update(assignment)
    }

    protected fun logStart(assignment: A) {
        logger.info("Starting assignment: $assignment")
    }
    protected fun logRefresh(assignment: A) {
        logger.info("Refreshing assignment: $assignment")
    }
    protected fun logCancel(assignment: A) {
        logger.info("Canceling assignment: $assignment")
    }

    protected fun setStatusInProgress(assignment: A) {
        assignment.status = AssignmentStatus.IN_PROGRESS
    }
    protected fun setStatusCompleted(assignment: A) {
        assignment.status = AssignmentStatus.COMPLETED
    }
    protected fun checkCompleted(assignment: A): Boolean {
        return assignment.status == AssignmentStatus.COMPLETED
    }

    protected fun scheduleRefresh(assignment: A) {
        val schedulingProps: SchedulingProperties? = assignment.refreshSchedulingProperties
        if (schedulingProps != null) {
            val task = SchedulingAssignmentTask(
                action = this::refresh,
                assignmentId = assignment.id
            )
            val schedulingInfo = SchedulingInfo(
                properties = schedulingProps,
                taskId = UUID.randomUUID(),
                status = SchedulingStatus.QUEUED
            )
            assignmentScheduler.start(
                task = task,
                taskId = schedulingInfo.taskId,
                interval = schedulingProps.interval
            )
            schedulingInfo.status = SchedulingStatus.ACTIVE
            assignment.initRefreshScheduling(schedulingInfo)
        }
    }

    protected fun stopSchedulingRefresh(assignment: A) {
        val schedulingInfo: SchedulingInfo? = assignment.getRefreshSchedulingInfo()
        if (schedulingInfo != null) {
            assignmentScheduler.stop(schedulingInfo.taskId)
            schedulingInfo.status = SchedulingStatus.COMPLETED
        }
    }
}
