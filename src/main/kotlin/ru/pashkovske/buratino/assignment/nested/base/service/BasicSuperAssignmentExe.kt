package ru.pashkovske.buratino.assignment.nested.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.nested.base.model.SuperAssignment

private val logger = KotlinLogging.logger {}

abstract class BasicSuperAssignmentExe<
    A : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    override val assignmentRepo: AssignmentRepo<A>,
    protected open val nestedAssignmentExe: AssignmentExe<Nested>
): BasicAssignmentExe<A>(assignmentRepo = assignmentRepo) {

    final override fun doStart(assignment: A) {
        if (assignment.nested.status == AssignmentStatus.QUEUED) {
            logger.info("Starting nested assignment: $assignment")
            assignment.nested = nestedAssignmentExe.start(assignment.nested)
        } else {
            logger.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping start")
        }
    }

    final override fun doRefresh(assignment: A) {
        logger.info("Refreshing nested assignment: ${assignment.nested.id}")
        when (assignment.nested.status) {
            AssignmentStatus.IN_PROGRESS -> {
                assignment.nested = nestedAssignmentExe.refresh(assignment.nested.id)
                logger.info("Nested assignment ${assignment.nested.id} refreshed")
            }
            AssignmentStatus.COMPLETED -> logger.info(
                "Nested assignment ${assignment.nested.id} is completed, skipping refresh"
            )
            else -> logger.warn(
                "Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping refresh"
            )
        }
    }

    final override fun doCancel(assignment: A) {
        logger.info("Cancelling nested assignment: ${assignment.nested.id}")
        when (assignment.nested.status) {
            AssignmentStatus.IN_PROGRESS -> {
                assignment.nested = nestedAssignmentExe.cancel(assignment.nested.id)
                logger.info("Nested assignment ${assignment.nested.id} cancelled")
            }
            AssignmentStatus.COMPLETED -> logger.info(
                "Nested assignment ${assignment.nested.id} is already completed, skipping cancel"
            )
            else -> logger.warn(
                "Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping cancel"
            )
        }
    }
}
