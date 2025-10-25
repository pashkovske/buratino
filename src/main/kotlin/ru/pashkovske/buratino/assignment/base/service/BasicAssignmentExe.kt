package ru.pashkovske.buratino.assignment.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class BasicAssignmentExe<A: Assignment>(
    protected open val assignmentRepo: AssignmentRepo<A>
): AssignmentExe<A> {
    override fun start(assignment: A): A {
        logger.info("Starting assignment: $assignment")

        doStart(assignment)

        assignment.status = AssignmentStatus.IN_PROGRESS
        assignmentRepo.create(assignment)
        return assignment
    }

    override fun refresh(id: UUID): A {
        logger.info("Refreshing assignment: $id")
        val assignment = assignmentRepo.get(id)
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping refresh")
            return assignment
        }

        doRefresh(assignment)

        assignmentRepo.update(assignment)
        return assignment
    }

    override fun cancel(id: UUID): A {
        logger.info("Cancelling assignment: $id")
        val assignment = assignmentRepo.get(id)
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping cancel")
            return assignment
        }

        doCancel(assignment)

        assignment.status = AssignmentStatus.COMPLETED
        assignmentRepo.update(assignment)
        return assignment
    }

    protected abstract fun doStart(assignment: A)

    protected abstract fun doRefresh(assignment: A)

    protected abstract fun doCancel(assignment: A)
}
