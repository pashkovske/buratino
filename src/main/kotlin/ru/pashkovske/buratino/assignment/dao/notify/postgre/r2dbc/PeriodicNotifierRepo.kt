package ru.pashkovske.buratino.assignment.dao.notify.postgre.r2dbc

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Flux
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicNotifierRow
import java.util.UUID

interface PeriodicNotifierRepo<Row: PeriodicNotifierRow> : R2dbcRepository<Row, UUID> {

    @Query("SELECT * FROM #{#tableName} WHERE assignment_id = :assignmentId")
    fun findByAssignmentId(@Param("assignmentId") assignmentId: UUID): Flux<Row>
}
