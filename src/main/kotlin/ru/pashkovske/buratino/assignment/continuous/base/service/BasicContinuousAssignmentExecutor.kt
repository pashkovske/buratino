package ru.pashkovske.buratino.assignment.continuous.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.InstrumentAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentExecutor
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExecutor
import ru.pashkovske.buratino.assignment.continuous.base.model.ContinuousAssignment
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class BasicContinuousAssignmentExecutor<
    T : ContinuousAssignment<NestedAssignment>,
    NestedAssignment : InstrumentAssignment
    >(
    override val assignmentRepo: AssignmentRepo<T>,
    protected open val nestedAssignmentExecutor: AssignmentExecutor<NestedAssignment>
):
    BasicAssignmentExecutor<T>(assignmentRepo = assignmentRepo),
    ContinuousAssignmentExecutor<T>
{
    override fun continueAssignment(id: UUID): T {
        val assignment = assignmentRepo.get(id)
        logger.info("Continuing assignment: $assignment")
        if (assignment.currentAssignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.currentAssignment.id} is completed, replacing with next one")
            assignment.currentAssignment = doContinue(assignment.currentAssignment)
            logger.info("Completed assignment replaced with ${assignment.currentAssignment.id}")
        } else {
            logger.warn("Assignment ${assignment.currentAssignment.id} is not completed, skipping continuation")
        }
        assignmentRepo.update(assignment)
        return assignment
    }

    protected abstract fun doContinue(assignment: NestedAssignment): NestedAssignment

    override fun doStart(assignment: T) {
        if (assignment.currentAssignment.status == AssignmentStatus.QUEUED) {
            logger.info("Starting nested assignment: $assignment")
            assignment.currentAssignment = nestedAssignmentExecutor.start(assignment.currentAssignment)
        } else {
            logger.warn("Nested assignment ${assignment.currentAssignment.id} is in ${assignment.currentAssignment.status} status, skipping start")
        }
    }

    override fun doRefresh(assignment: T) {
        logger.info("Refreshing nested assignment: ${assignment.currentAssignment.id}")
        when (assignment.currentAssignment.status) {
            AssignmentStatus.IN_PROGRESS -> {
                assignment.currentAssignment = nestedAssignmentExecutor.refresh(assignment.currentAssignment.id)
                logger.info("Nested assignment ${assignment.currentAssignment.id} refreshed")
            }
            AssignmentStatus.COMPLETED -> logger.info(
                "Nested assignment ${assignment.currentAssignment.id} is completed, skipping refresh"
            )
            else -> logger.warn(
                "Nested assignment ${assignment.currentAssignment.id} is in ${assignment.currentAssignment.status} status, skipping refresh"
            )
        }
    }

    override fun doCancel(assignment: T) {
        logger.info("Cancelling nested assignment: ${assignment.currentAssignment.id}")
        when (assignment.currentAssignment.status) {
            AssignmentStatus.IN_PROGRESS -> {
                assignment.currentAssignment = nestedAssignmentExecutor.cancel(assignment.currentAssignment.id)
                logger.info("Nested assignment ${assignment.currentAssignment.id} cancelled")
            }
            AssignmentStatus.COMPLETED -> logger.info(
                "Nested assignment ${assignment.currentAssignment.id} is already completed, skipping cancel"
            )
            else -> logger.warn(
                "Nested assignment ${assignment.currentAssignment.id} is in ${assignment.currentAssignment.status} status, skipping cancel"
            )
        }
    }
}