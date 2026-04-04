package ru.pashkovske.buratino.assignment.dao.core.postgre

import org.springframework.context.annotation.DependsOn
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.mapper.AssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.exception.AssignmentDaoOperationException
import ru.pashkovske.buratino.assignment.model.AssignmentState
import java.util.UUID

@DependsOn("flywayInitializer")
abstract class PostgreAssignmentDao<A : Assignment, Row : AssignmentPostgreRow<A>>(
    protected open val mapper: AssignmentToPostgreMapper<A, Row>,
    protected val r2dbcRepository: AssignmentR2dbcRepo<A, Row>,
    protected val r2dbcEntityTemplate: R2dbcEntityTemplate
) : AssignmentDao<A> {

    override fun getAll(): List<A> {
        return r2dbcRepository.findAll()
            .map(mapper::map)
            .collectList()
            .block() ?: emptyList()
    }

    override fun getByState(state: AssignmentState): List<A> {
        return r2dbcRepository.findByState(AssignmentState.IN_PROGRESS)
            .map(mapper::map)
            .collectList()
            .block() ?: throw AssignmentDaoOperationException(
                message = "Assignments not found in PostgreSQL",
                assignmentId = null
            )
    }

    override fun get(id: UUID): A {
        return find(id) ?: throw AssignmentDaoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id
            )
    }

    override fun find(id: UUID): A? {
        return r2dbcRepository.findById(id)
            .map(mapper::map)
            .block()
    }

    override fun create(assignment: A) {
        r2dbcEntityTemplate.insert(mapper.map(assignment))
            .block()
    }

    override fun update(assignment: A) {
        r2dbcEntityTemplate.update(mapper.map(assignment))
            .block()
    }

    override fun deleteAll() {
        r2dbcRepository.deleteAll()
            .block()
    }
}
