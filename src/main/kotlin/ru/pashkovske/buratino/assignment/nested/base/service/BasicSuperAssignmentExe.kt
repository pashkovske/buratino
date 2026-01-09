package ru.pashkovske.buratino.assignment.nested.base.service

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.nested.base.model.SuperAssignment

abstract class BasicSuperAssignmentExe<
    A : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentRepo: AssignmentRepo<A>,
    assignmentScheduler: AssignmentTaskScheduler,
    protected val nestedAssignmentExe: AssignmentExe<Nested>
): BasicAssignmentExe<A>(
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    protected fun checkNestedCompleted(assignment: A): Boolean {
        return assignment.nested.status == AssignmentStatus.COMPLETED
    }

    protected fun checkAndStartNested(assignment: A): A {
        when (assignment.nested.status) {
            AssignmentStatus.QUEUED -> {
                log.info("Starting nested assignment: ${assignment.nested.id}")
                assignment.nested = nestedAssignmentExe.start(assignment.nested)
            }
            else ->
                log.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping start")
        }
        return assignment
    }

    protected fun refreshNested(assignment: A): A {
        assignment.nested = nestedAssignmentExe.refresh(assignment.nested.id)
        return assignment
    }

    protected fun cancelNested(assignment: A): A {
        when (assignment.nested.status) {
            AssignmentStatus.IN_PROGRESS -> {
                log.info("Canceling nested assignment: ${assignment.nested.id}")
                assignment.nested = nestedAssignmentExe.cancel(assignment.nested.id)
            }
            else ->
                log.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping cancel")
        }
        return assignment
    }
}
