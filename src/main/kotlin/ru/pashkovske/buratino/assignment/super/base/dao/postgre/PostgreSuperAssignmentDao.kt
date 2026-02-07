package ru.pashkovske.buratino.assignment.`super`.base.dao.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDaoOperationException
import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.dao.postgre.PostgreAssignmentDao
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import java.util.UUID

abstract class PostgreSuperAssignmentDao<
    NestedA : Assignment,
    SuperA : SuperAssignment<NestedA>,
    NestedRow : AssignmentPostgreRow<NestedA>,
    SuperRow : SuperAssignmentPostgreRow<SuperA>
    >(
    override val mapper: SuperAssignmentToPostgreMapper<NestedA, SuperA, SuperRow>,
    r2dbcRepository: ReactiveCrudRepository<SuperRow, UUID>,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val nestedAssignmentDao: PostgreAssignmentDao<NestedA, NestedRow>
) : PostgreAssignmentDao<SuperA, SuperRow>(
    mapper,
    r2dbcRepository,
    r2dbcEntityTemplate
) {

    override fun getAll(): List<SuperA> {
        val superAssignmentRows: List<SuperRow> = r2dbcRepository.findAll()
            .collectList()
            .block() ?: emptyList()
        val nestedAssignmentsMap: Map<UUID, NestedA> = nestedAssignmentDao.getAll()
            .associateBy { it.id }
        return superAssignmentRows.map { superAssignmentRow ->
            val nestedAssignment: NestedA = nestedAssignmentsMap[superAssignmentRow.nestedAssignmentId] ?:
            throw AssignmentDaoOperationException(
                message = "Nested assignment ${superAssignmentRow.nestedAssignmentId} not found in PostgreSQL",
                assignmentId = superAssignmentRow.id
            )
            mapper.map(
                superRow = superAssignmentRow,
                nestedAssignment = nestedAssignment
            )
        }
    }

    override fun get(id: UUID): SuperA {
        val superAssignmentRow: SuperRow = r2dbcRepository.findById(id)
            .block() ?: throw AssignmentDaoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id
            )
        val nestedAssignment: NestedA = nestedAssignmentDao.get(superAssignmentRow.nestedAssignmentId)
        return mapper.map(
            superRow = superAssignmentRow,
            nestedAssignment = nestedAssignment
        )
    }
}
