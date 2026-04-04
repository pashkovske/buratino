package ru.pashkovske.buratino.assignment.service.core.refresh.dispatcher

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.refresh.ContinuousFractionalSpreadAssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.refresh.FractionalSpreadAssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.refresh.TopPriceAssignmentRefresher
import java.util.UUID

@Service
class RefreshDispatcherImpl(
    private val continuousFractionalSpreadAssignmentRefresher: ContinuousFractionalSpreadAssignmentRefresher,
    private val topPriceAssignmentRefresher: TopPriceAssignmentRefresher,
    private val fractionalSpreadAssignmentRefresher: FractionalSpreadAssignmentRefresher,
    private val daos: List<AssignmentDao<out Assignment>>
) : RefreshDispatcher {

    fun findAssignment(id: UUID): Assignment {
        for (dao: AssignmentDao<out Assignment> in daos) {
            val assignment: Assignment? = dao.find(id)
            if (assignment != null) {
                return assignment
            }
        }
        throw IllegalArgumentException("""
            There is no assignment with id $id in database.
            Searched via daos:
                ${daos.joinToString(",\n\t\t\t") { it.javaClass.simpleName }}
        """.trimIndent())
    }

    private fun getRefresher(assignment: Assignment): AssignmentRefresher<out Assignment> {
        return when (assignment) {
            is ContinuousFractionalSpreadAssignment -> continuousFractionalSpreadAssignmentRefresher
            is TopPriceAssignment -> topPriceAssignmentRefresher
            is FractionalSpreadAssignment -> fractionalSpreadAssignmentRefresher
        }
    }

    override fun getRefresher(assignmentId: UUID): AssignmentRefresher<out Assignment> {
        return getRefresher(
            assignment = findAssignment(assignmentId)
        )
    }
}
