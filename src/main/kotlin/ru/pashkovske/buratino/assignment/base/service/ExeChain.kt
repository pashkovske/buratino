package ru.pashkovske.buratino.assignment.base.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.continuous.base.service.ContinuousAssignmentExe
import java.util.UUID

@Service
class ExeChain {
    fun <A: Assignment> start(
        assignment: A,
        exe: AssignmentExe<A>
    ): A {
        val assignment: A = exe.start(assignment)
        return assignment
    }

    fun <A: Assignment> refresh(
        id: UUID,
        exe: AssignmentExe<A>
    ): A {
        val refreshedAssignment: A = exe.refresh(id)
        return refreshedAssignment
    }

    fun <A: Assignment> cancel(
        id: UUID,
        exe: AssignmentExe<A>
    ): A {
        val cancelledAssignment: A = exe.cancel(id)
        return cancelledAssignment
    }

    fun <A: ContinuousAssignment<NA>, NA: Assignment> continueAssignment(
        id: UUID,
        exe: ContinuousAssignmentExe<A>
    ): A {
        val continuedAssignment = exe.continueAssignment(id)
        return continuedAssignment
    }
}
