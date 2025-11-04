package ru.pashkovske.buratino.assignment.nested.continuous.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentAction
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionChain
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionResult
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
    assignmentRepo: AssignmentRepo<CA>,
    nestedAssignmentExe: AssignmentExe<Nested>
):
    BasicSuperAssignmentExe<CA, Nested>(
        assignmentRepo = assignmentRepo,
        nestedAssignmentExe = nestedAssignmentExe
    ),
    ContinuousAssignmentExe<CA>
{
    protected val continueAssignmentChain: AssignmentActionChain<CA> = AssignmentActionChain()

    init {
        initContinueChain()
    }

    protected fun initContinueChain() {
        continueAssignmentChain += AssignmentAction(
            name = "log_continue",
            action = { assignment ->
                logger.info("Continuing assignment: $assignment")
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        continueAssignmentChain += AssignmentAction(
            name = "check_status",
            action = { assignment ->
                val isCompleted = assignment.status == AssignmentStatus.COMPLETED
                if (isCompleted) {
                    logger.info("Assignment ${assignment.id} is already completed, skipping continuation")
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = !isCompleted
                )
            }
        )
        continueAssignmentChain += AssignmentAction(
            name = "check_nested_status",
            action = { assignment ->
                val isCompleted = assignment.nested.status == AssignmentStatus.COMPLETED
                if (!isCompleted) {
                    logger.warn("Assignment ${assignment.nested.id} is not completed, skipping continuation")
                } else {
                    logger.info("Assignment ${assignment.nested.id} is completed, continuing")
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = isCompleted
                )
            }
        )
        continueAssignmentChain += updateInRepoAction
    }

    final override fun continueAssignment(id: UUID): CA {
        return continueAssignmentChain(assignmentRepo.get(id))
    }

    fun getContinueChainNames(): List<String> {
        return continueAssignmentChain.getNames()
    }
}
