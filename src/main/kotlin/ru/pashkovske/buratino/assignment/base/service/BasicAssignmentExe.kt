package ru.pashkovske.buratino.assignment.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentAction
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionChain
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionResult
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class BasicAssignmentExe<A: Assignment>(
    protected val assignmentRepo: AssignmentRepo<A>,
    protected val assignmentScheduler: AssignmentTaskScheduler
): AssignmentExe<A> {
    protected val startAssignmentChain: AssignmentActionChain<A> = AssignmentActionChain()
    protected val refreshAssignmentChain: AssignmentActionChain<A> = AssignmentActionChain()
    protected val cancelAssignmentChain: AssignmentActionChain<A> = AssignmentActionChain()

    protected val updateInRepoAction: AssignmentAction<A> = AssignmentAction(
        name = "update_in_repo",
        action = { assignment ->
            assignmentRepo.update(assignment)
            AssignmentActionResult(
                assignment = assignment,
                shouldContinue = true
            )
        }
    )

    init {
        initStartChain()
        initRefreshChain()
        initCancelChain()
    }

    private fun initStartChain() {
        startAssignmentChain += AssignmentAction(
            name = "log_start",
            action = { assignment ->
                logger.info("Starting assignment: $assignment")
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        startAssignmentChain += AssignmentAction(
            name = "set_status_in_progress",
            action = { assignment ->
                assignment.status = AssignmentStatus.IN_PROGRESS
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        startAssignmentChain += AssignmentAction(
            name = "create_in_repo",
            action = { assignment ->
                assignmentRepo.create(assignment)
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    private fun initRefreshChain() {
        refreshAssignmentChain += AssignmentAction(
            name = "log_refresh",
            action = { assignment ->
                logger.info("Refreshing assignment: $assignment")
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        refreshAssignmentChain += AssignmentAction(
            name = "check_completed",
            action = { assignment ->
                val isCompleted: Boolean = assignment.status == AssignmentStatus.COMPLETED
                if (isCompleted) {
                    logger.info("Assignment ${assignment.id} is already completed, skipping refresh")
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = !isCompleted
                )
            }
        )
        refreshAssignmentChain += AssignmentAction(
            name = "set_status_in_progress",
            action = { assignment ->
                assignment.status = AssignmentStatus.IN_PROGRESS
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        refreshAssignmentChain += updateInRepoAction
    }

    private fun initCancelChain() {
        cancelAssignmentChain += AssignmentAction(
            name = "log_cancel",
            action = { assignment ->
                logger.info("Cancelling assignment: $assignment")
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        cancelAssignmentChain += AssignmentAction(
            name = "check_completed",
            action = { assignment ->
                val isCompleted: Boolean = assignment.status == AssignmentStatus.COMPLETED
                if (isCompleted) {
                    logger.info("Assignment ${assignment.id} is already completed, skipping cancel")
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = !isCompleted
                )
            }
        )
        cancelAssignmentChain += AssignmentAction(
            name = "set_status_completed",
            action = { assignment ->
                assignment.status = AssignmentStatus.COMPLETED
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
        cancelAssignmentChain += updateInRepoAction
    }

    final override fun start(assignment: A): A {
        return startAssignmentChain(assignment)
    }

    final override fun refresh(id: UUID): A {
        return refreshAssignmentChain(assignmentRepo.get(id))
    }

    final override fun cancel(id: UUID): A {
        return cancelAssignmentChain(assignmentRepo.get(id))
    }

    fun getStartChainNames(): List<String> {
        return startAssignmentChain.getNames()
    }

    fun getRefreshChainNames(): List<String> {
        return refreshAssignmentChain.getNames()
    }

    fun getCancelChainNames(): List<String> {
        return cancelAssignmentChain.getNames()
    }
}
