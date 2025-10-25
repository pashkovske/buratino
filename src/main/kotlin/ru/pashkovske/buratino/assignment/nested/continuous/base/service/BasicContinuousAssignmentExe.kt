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
    T : ContinuousAssignment<NestedAssignment>,
    NestedAssignment : Assignment
    >(
    override val assignmentRepo: AssignmentRepo<T>,
    nestedAssignmentExe: AssignmentExe<NestedAssignment>
):
    BasicSuperAssignmentExe<T, NestedAssignment>(
        assignmentRepo = assignmentRepo,
        nestedAssignmentExe = nestedAssignmentExe
    ),
    ContinuousAssignmentExe<T>
{
    override fun continueAssignment(id: UUID): T {
        val assignment = assignmentRepo.get(id)
        logger.info("Continuing assignment: $assignment")
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

    protected abstract fun doContinue(assignment: NestedAssignment): NestedAssignment
}
