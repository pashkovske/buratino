package ru.pashkovske.buratino.assignment.nested.continuous.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.nested.base.service.BasicSuperAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.base.model.ContinuousAssignment
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class BasicContinuousAssignmentExe<
    CA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    override val assignmentRepo: AssignmentRepo<CA>,
    nestedAssignmentExe: AssignmentExe<Nested>
):
    BasicSuperAssignmentExe<CA, Nested>(
        assignmentRepo = assignmentRepo,
        nestedAssignmentExe = nestedAssignmentExe
    ),
    ContinuousAssignmentExe<CA>
{
    override fun continueAssignment(id: UUID): CA {
        val assignment = assignmentRepo.get(id)
        logger.info("Continuing assignment: $assignment")
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping continuation")
            return assignment
        }
        if (assignment.nested.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.nested.id} is completed, replacing with next one")
            assignment.nested = doContinue(assignment.nested)
            logger.info("Completed assignment replaced with ${assignment.nested.id}")
        } else {
            logger.warn("Assignment ${assignment.nested.id} is not completed, skipping continuation")
        }
        assignmentRepo.update(assignment)
        return assignment
    }

    protected abstract fun doContinue(assignment: Nested): Nested
}
