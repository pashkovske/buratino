package ru.pashkovske.buratino.assignment.nested.continuous.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentAction
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionChain
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionResult
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingAssignmentTask
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.nested.base.service.BasicSuperAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.base.model.ContinuousAssignment
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class BasicContinuousAssignmentExe<
    CA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentRepo: AssignmentRepo<CA>,
    assignmentScheduler: AssignmentTaskScheduler,
    nestedAssignmentExe: AssignmentExe<Nested>
):
    BasicSuperAssignmentExe<CA, Nested>(
        assignmentRepo = assignmentRepo,
        nestedAssignmentExe = nestedAssignmentExe,
        assignmentScheduler = assignmentScheduler
    ),
    ContinuousAssignmentExe<CA>
{
    protected val continueAssignmentChain: AssignmentActionChain<CA> = AssignmentActionChain()

    init {
        initContinueChain()
        addContinueSchedulingToStartChain()
        addStopSchedulingToCancelChain()
    }

    private fun addContinueSchedulingToStartChain() {
        startAssignmentChain["check_and_start_nested"] = AssignmentAction(
            name = "schedule_continuation",
            action = { assignment: CA ->
                val schedulingProps: SchedulingProperties? = assignment.schedulingProperties
                if (schedulingProps != null) {
                    val task = SchedulingAssignmentTask(
                        actionChain = continueAssignmentChain,
                        assignment = assignment
                    )
                    val schedulingInfo = SchedulingInfo(
                        properties = schedulingProps,
                        taskId = UUID.randomUUID(),
                        task = task
                    )
                    assignmentScheduler.start(
                        task = task,
                        taskId = schedulingInfo.taskId,
                        interval = schedulingProps.interval
                    )
                    schedulingInfo.status = SchedulingStatus.ACTIVE
                    assignment.initContinueScheduling(schedulingInfo)
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    private fun addStopSchedulingToCancelChain() {
        cancelAssignmentChain["check_completed"] = AssignmentAction(
            name = "stop_scheduling_continuation",
            action = { assignment: CA ->
                val schedulingInfo: SchedulingInfo? = assignment.getContinueSchedulingInfo()
                if (schedulingInfo != null) {
                    assignmentScheduler.stop(schedulingInfo.taskId)
                    schedulingInfo.status = SchedulingStatus.COMPLETED
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
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
