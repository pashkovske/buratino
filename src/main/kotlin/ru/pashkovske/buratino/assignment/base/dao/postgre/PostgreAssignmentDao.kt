package ru.pashkovske.buratino.assignment.base.dao.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDaoOperationException
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import java.util.UUID

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

    override fun getByStatus(status: AssignmentStatus): List<A> {
        return r2dbcRepository.findByStatus(AssignmentStatus.IN_PROGRESS)
            .map(mapper::map)
            .collectList()
            .block() ?: throw AssignmentDaoOperationException(
                message = "Assignments not found in PostgreSQL",
                assignmentId = null
            )
    }

    override fun get(id: UUID): A {
        return r2dbcRepository.findById(id)
            .map(mapper::map)
            .block() ?: throw AssignmentDaoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id
            )
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
