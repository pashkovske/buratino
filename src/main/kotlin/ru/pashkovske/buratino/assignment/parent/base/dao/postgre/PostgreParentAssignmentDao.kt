package ru.pashkovske.buratino.assignment.parent.base.dao.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDaoOperationException
import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.base.dao.postgre.PostgreAssignmentDao
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
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
        val parentAssignmentRow: ParentRow = r2dbcRepository.findById(id)
            .block() ?: throw AssignmentDaoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id
            )
        val childAssignment: ChildA = childAssignmentDao.get(parentAssignmentRow.childAssignmentId)
        return mapper.map(
            parentRow = parentAssignmentRow,
            childAssignment = childAssignment
        )
    }
}
