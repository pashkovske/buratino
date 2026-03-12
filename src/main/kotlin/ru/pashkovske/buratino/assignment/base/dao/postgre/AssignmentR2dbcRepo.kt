package ru.pashkovske.buratino.assignment.base.dao.postgre

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import java.util.UUID

interface AssignmentR2dbcRepo<
    A : Assignment,
    Row : AssignmentPostgreRow<A>
    > : ReactiveCrudRepository<Row, UUID> {

    @Query("SELECT * FROM #{#tableName} WHERE status = :status")
    fun findByStatus(@Param("status") status: AssignmentState): Flux<Row>
}
