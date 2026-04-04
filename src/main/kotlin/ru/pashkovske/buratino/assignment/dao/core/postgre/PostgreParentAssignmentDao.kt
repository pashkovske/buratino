package ru.pashkovske.buratino.assignment.dao.core.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import ru.pashkovske.buratino.assignment.dao.core.postgre.mapper.ParentAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ParentAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.exception.AssignmentDaoOperationException
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import java.util.UUID

abstract class PostgreParentAssignmentDao<
    ChildA : Assignment,
    ParentA : ParentAssignment<ChildA>,
    ChildRow : AssignmentPostgreRow<ChildA>,
    ParentRow : ParentAssignmentPostgreRow<ParentA>
    >(
    override val mapper: ParentAssignmentToPostgreMapper<ChildA, ParentA, ParentRow>,
    r2dbcRepository: AssignmentR2dbcRepo<ParentA, ParentRow>,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val childAssignmentDao: PostgreAssignmentDao<ChildA, ChildRow>
) : PostgreAssignmentDao<ParentA, ParentRow>(
    mapper,
    r2dbcRepository,
    r2dbcEntityTemplate
) {

    override fun getAll(): List<ParentA> {
        val parentAssignmentRows: List<ParentRow> = r2dbcRepository.findAll()
            .collectList()
            .block() ?: emptyList()
        val childAssignmentsMap: Map<UUID, ChildA> = childAssignmentDao.getAll()
            .associateBy { it.id }
        return parentAssignmentRows.map { parentAssignmentRow ->
            val childAssignment: ChildA = childAssignmentsMap[parentAssignmentRow.childAssignmentId] ?:
            throw AssignmentDaoOperationException(
                message = "Child assignment ${parentAssignmentRow.childAssignmentId} not found in PostgreSQL",
                assignmentId = parentAssignmentRow.id
            )
            mapper.map(
                parentRow = parentAssignmentRow,
                childAssignment = childAssignment
            )
        }
    }

    override fun getByState(state: AssignmentState): List<ParentA> {
        val parentAssignmentRows: List<ParentRow> = r2dbcRepository.findByState(state)
            .collectList()
            .block() ?: emptyList()
        val childAssignmentsMap: Map<UUID, ChildA> = childAssignmentDao.getByState(state)
            .associateBy { it.id }
        return parentAssignmentRows.map { parentAssignmentRow ->
            val childAssignment: ChildA = childAssignmentsMap[parentAssignmentRow.childAssignmentId] ?:
            throw AssignmentDaoOperationException(
                message = "Child assignment ${parentAssignmentRow.childAssignmentId} not found in PostgreSQL",
                assignmentId = parentAssignmentRow.id
            )
            mapper.map(
                parentRow = parentAssignmentRow,
                childAssignment = childAssignment
            )
        }
    }

    override fun get(id: UUID): ParentA {
        return find(id) ?: throw AssignmentDaoOperationException(
            message = "Assignment not found in PostgreSQL",
            assignmentId = id
        )
    }

    override fun find(id: UUID): ParentA? {
        val parentAssignmentRow: ParentRow = r2dbcRepository.findById(id)
            .block() ?: return null
        val childAssignment: ChildA = childAssignmentDao.find(parentAssignmentRow.childAssignmentId) ?: throw AssignmentDaoOperationException(
            message = "Child assignment `${parentAssignmentRow.childAssignmentId}` not found in PostgreSQL",
            assignmentId = parentAssignmentRow.id
        )
        return mapper.map(
            parentRow = parentAssignmentRow,
            childAssignment = childAssignment
        )
    }
}