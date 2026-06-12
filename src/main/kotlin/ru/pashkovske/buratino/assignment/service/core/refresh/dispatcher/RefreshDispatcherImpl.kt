package ru.pashkovske.buratino.assignment.service.core.refresh.dispatcher

import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import java.util.UUID

@Service
class RefreshDispatcherImpl(
    private val applicationContext: ApplicationContext,
    private val daos: List<AssignmentDao<out Assignment>>
) : RefreshDispatcher {

    lateinit var refreshers: Map<String, AssignmentRefresher<out Assignment>>

    @PostConstruct
    private fun registerRefreshers() {
        refreshers = applicationContext.getBeansOfType(AssignmentRefresher::class.java)
    }

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
        val refresherBeanName: String = when (assignment) {
            is RepeatableFractionalSpreadAssignment -> "repeatableFractionalSpreadAssignmentRefresher"
            is TopPriceAssignment -> "topPriceAssignmentRefresher"
            is FractionalSpreadAssignment -> "fractionalSpreadAssignmentRefresher"
        }
        return refreshers[refresherBeanName] ?: throw IllegalArgumentException(
            "Refresher for assignment of type ${assignment.javaClass.simpleName} not found"
        )
    }

    override fun getRefresher(assignmentId: UUID): AssignmentRefresher<out Assignment> {
        return getRefresher(
            assignment = findAssignment(assignmentId)
        )
    }
}
