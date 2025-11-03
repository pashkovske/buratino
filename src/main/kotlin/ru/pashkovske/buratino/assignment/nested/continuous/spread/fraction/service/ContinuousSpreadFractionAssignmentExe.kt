package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.AssignmentAction
import ru.pashkovske.buratino.assignment.base.model.AssignmentActionResult
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.nested.continuous.base.service.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model.ContinuousSpreadFractionAssignment
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
    init {
        addReplaceCompletedAssignmentToChain()
    }

    private fun addReplaceCompletedAssignmentToChain() {
        continueAssignmentChain["check_nested_status"] = AssignmentAction(
            name = "start_new_spread_fraction_assignment",
            action = { assignment: ContinuousSpreadFractionAssignment ->
                val completedAssignment = assignment.nested
                val nextAssignment = FractionalSpreadAssignment(
                    iid = completedAssignment.iid,
                    direction = completedAssignment.direction.getOpposite(),
                    rate = completedAssignment.rate
                )
                nestedAssignmentExe.start(nextAssignment)
                assignment.nested = nextAssignment
                logger.info("Started new spread fraction assignment: ${nextAssignment.id}")
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }
}
