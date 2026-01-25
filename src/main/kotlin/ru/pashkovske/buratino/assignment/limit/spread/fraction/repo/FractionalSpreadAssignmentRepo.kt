package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepoOperationException
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import java.util.UUID

@Repository
class FractionalSpreadAssignmentRepo(
    private val r2dbcRepo: FractionalSpreadAssignmentRepoPostgre,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
): AssignmentRepo<FractionalSpreadAssignment> {

    override fun getAll(): List<FractionalSpreadAssignment> {
        return r2dbcRepo.findAll()
            .map(FractionalSpreadAssignmentMapper::map)
            .collectList()
            .block() ?: emptyList()
    }

    override fun get(id: UUID): FractionalSpreadAssignment {
        return r2dbcRepo.findById(id)
            .map(FractionalSpreadAssignmentMapper::map)
            .block() ?: throw AssignmentRepoOperationException(
                message = "Assignment not found in PostgreSQL",
                assignmentId = id,
                assignmentClass = FractionalSpreadAssignment::class
            )
    }

    override fun create(assignment: FractionalSpreadAssignment) {
        r2dbcEntityTemplate.insert(FractionalSpreadAssignmentMapper.map(assignment))
            .block()
    }

    override fun update(assignment: FractionalSpreadAssignment) {
        r2dbcEntityTemplate.update(FractionalSpreadAssignmentMapper.map(assignment))
            .block()
    }

    override fun deleteAll() {
        r2dbcRepo.deleteAll()
            .block()
    }
}
