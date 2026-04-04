package ru.pashkovske.buratino.assignment.service.core.continuation.dispatcher

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousFractionalSpreadAssignmentContinuer
import java.util.UUID

@Service
class ContinueDispatcherImpl(
    private val continuousFractionalSpreadAssignmentContinuer: ContinuousFractionalSpreadAssignmentContinuer,
    private val daos: List<AssignmentDao<out ContinuousAssignment<out Assignment>>>
) : ContinueDispatcher {

    private fun findAssignment(id: UUID): ContinuousAssignment<out Assignment> {
        for (dao: AssignmentDao<out ContinuousAssignment<out Assignment>> in daos) {
            val assignment: ContinuousAssignment<out Assignment>? = dao.find(id)
            if (assignment != null) {
                return assignment
            }
        }
        throw IllegalArgumentException("""
            Continuous assignment with id $id not found in database.
            Searched via daos:
                ${daos.joinToString(",\n    ") { it.javaClass.simpleName }}
        """.trimIndent())
    }

    private fun getContinuer(
        assignment: ContinuousAssignment<out Assignment>
    ): ContinuousAssignmentContinuer<out ContinuousAssignment<out Assignment>> {
        return when (assignment) {
            is ContinuousFractionalSpreadAssignment -> continuousFractionalSpreadAssignmentContinuer
        }
    }

    override fun getContinuer(assignmentId: UUID): ContinuousAssignmentContinuer<out ContinuousAssignment<out Assignment>> {
        return getContinuer(
            assignment = findAssignment(assignmentId)
        )
    }
}
