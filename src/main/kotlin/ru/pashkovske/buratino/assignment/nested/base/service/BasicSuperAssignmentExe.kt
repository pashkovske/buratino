package ru.pashkovske.buratino.assignment.nested.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentAction
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionResult
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
    assignmentRepo: AssignmentRepo<A>,
    protected val nestedAssignmentExe: AssignmentExe<Nested>
): BasicAssignmentExe<A>(assignmentRepo = assignmentRepo) {

    init {
        addStartNestedToChain()
        addRefreshNestedToChain()
        addCancelNestedToChain()
    }

    private fun addStartNestedToChain() {
        startAssignmentChain["log_start"] = AssignmentAction(
            name = "check_and_start_nested",
            action = { assignment ->
                when (assignment.nested.status) {
                    AssignmentStatus.QUEUED -> {
                        logger.info("Starting nested assignment: ${assignment.nested.id}")
                        assignment.nested = nestedAssignmentExe.start(assignment.nested)
                    }
                    else ->
                        logger.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping start")
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    private fun addRefreshNestedToChain() {
        refreshAssignmentChain["check_completed"] = AssignmentAction(
            name = "refresh_nested",
            action = { assignment ->
                when (assignment.nested.status) {
                    AssignmentStatus.IN_PROGRESS -> {
                        assignment.nested = nestedAssignmentExe.refresh(assignment.nested.id)
                        logger.info("Nested assignment ${assignment.nested.id} refreshed")
                    }
                    AssignmentStatus.COMPLETED ->
                        logger.info("Nested assignment ${assignment.nested.id} is completed, skipping refresh nested")
                    else ->
                        logger.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping refresh nested")
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    private fun addCancelNestedToChain() {
        cancelAssignmentChain["check_completed"] = AssignmentAction(
            name = "cancel_nested",
            action = { assignment ->
                when (assignment.nested.status) {
                    AssignmentStatus.COMPLETED -> logger.info(
                        "Nested assignment ${assignment.nested.id} is already completed, skipping cancel nested"
                    )
                    else -> {
                        assignment.nested = nestedAssignmentExe.cancel(assignment.nested.id)
                        logger.info("Nested assignment ${assignment.nested.id} cancelled")
                    }
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }
}
