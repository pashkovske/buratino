package ru.pashkovske.buratino.assignment.base.repo.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepoOperationException
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import java.util.UUID

abstract class PostgreAssignmentRepo<A : Assignment, Row>(
    private val mapper: AssignmentToPostgreMapper<A, Row>,
    private val r2dbcRepository: ReactiveCrudRepository<Row, UUID>,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : AssignmentRepo<A> {

    override fun getAll(): List<A> {
        return r2dbcRepository.findAll()
            .map(mapper::map)
            .collectList()
            .block() ?: emptyList()
    }

    override fun get(id: UUID): A {
        return r2dbcRepository.findById(id)
            .map(mapper::map)
            .block() ?: throw AssignmentRepoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id,
                assignmentClass = FractionalSpreadAssignment::class
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
