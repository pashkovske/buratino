package ru.pashkovske.buratino.assignment.continuous.spread.fraction.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.continuous.base.service.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.continuous.spread.fraction.model.ContinuousSpreadFractionAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe

private val logger = KotlinLogging.logger {}

@Service
class ContinuousSpreadFractionAssignmentExe(
    override val assignmentRepo: AssignmentRepo<ContinuousSpreadFractionAssignment>,
    override val nestedAssignmentExe: FractionalSpreadAssignmentExe
): BasicContinuousAssignmentExe<
    ContinuousSpreadFractionAssignment,
    FractionalSpreadAssignment
    >(
    assignmentRepo = assignmentRepo,
    nestedAssignmentExe = nestedAssignmentExe
) {
    override fun doContinue(assignment: FractionalSpreadAssignment): FractionalSpreadAssignment {
        if (assignment.status == AssignmentStatus.COMPLETED) {
            val nextAssignment = FractionalSpreadAssignment(
                iid = assignment.iid,
                direction = assignment.direction.getOpposite(),
                rate = assignment.rate
            )
            return nestedAssignmentExe.start(nextAssignment)
        } else {
            logger.warn("Nested assignment ${assignment.id} is not competed, is in ${assignment.status} status, skipping continue")
            return assignment
        }
    }
}
