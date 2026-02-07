package ru.pashkovske.buratino.assignment.nested.base.repo.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepoOperationException
import ru.pashkovske.buratino.assignment.base.repo.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.repo.postgre.PostgreAssignmentRepo
import ru.pashkovske.buratino.assignment.nested.base.model.SuperAssignment
import java.util.UUID

abstract class PostgreSuperAssignmentRepo<
    NestedA : Assignment,
    SuperA : SuperAssignment<NestedA>,
    NestedRow : AssignmentPostgreRow<NestedA>,
    SuperRow : SuperAssignmentPostgreRow<SuperA>
    >(
    override val mapper: SuperAssignmentToPostgreMapper<NestedA, SuperA, SuperRow>,
    r2dbcRepository: ReactiveCrudRepository<SuperRow, UUID>,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val nestedAssignmentRepo: PostgreAssignmentRepo<NestedA, NestedRow>
) : PostgreAssignmentRepo<SuperA, SuperRow>(
    mapper,
    r2dbcRepository,
    r2dbcEntityTemplate
) {

    override fun getAll(): List<SuperA> {
        val superAssignmentRows: List<SuperRow> = r2dbcRepository.findAll()
            .collectList()
            .block() ?: emptyList()
        val nestedAssignmentsMap: Map<UUID, NestedA> = nestedAssignmentRepo.getAll()
            .associateBy { it.id }
        return superAssignmentRows.map { superAssignmentRow ->
            val nestedAssignment: NestedA = nestedAssignmentsMap[superAssignmentRow.nestedAssignmentId] ?:
            throw AssignmentRepoOperationException(
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
            .block() ?: throw AssignmentRepoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id
            )
        val nestedAssignment: NestedA = nestedAssignmentRepo.get(superAssignmentRow.nestedAssignmentId)
        return mapper.map(
            superRow = superAssignmentRow,
            nestedAssignment = nestedAssignment
        )
    }
}
