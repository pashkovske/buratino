package ru.pashkovske.buratino.assignment.continuous.spread.fraction.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.continuous.base.service.BasicContinuousAssignmentExecutor
import ru.pashkovske.buratino.assignment.continuous.spread.fraction.model.ContinuousSpreadFractionAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExecutor

private val logger = KotlinLogging.logger {}

@Service
class ContinuousSpreadFractionAssignmentExecutor(
    override val assignmentRepo: AssignmentRepo<ContinuousSpreadFractionAssignment>,
    override val nestedAssignmentExecutor: FractionalSpreadAssignmentExecutor
): BasicContinuousAssignmentExecutor<
    ContinuousSpreadFractionAssignment,
    FractionalSpreadAssignment
    >(
    assignmentRepo = assignmentRepo,
    nestedAssignmentExecutor = nestedAssignmentExecutor
) {
    override fun doContinue(assignment: FractionalSpreadAssignment): FractionalSpreadAssignment {
        if (assignment.status == AssignmentStatus.COMPLETED) {
            val nextAssignment = FractionalSpreadAssignment(
                iid = assignment.iid,
                direction = assignment.direction.getOpposite(),
                rate = assignment.rate
            )
            return nestedAssignmentExecutor.start(nextAssignment)
        } else {
            logger.warn("Nested assignment ${assignment.id} is not competed, is in ${assignment.status} status, skipping continue")
            return assignment
        }
    }
}
