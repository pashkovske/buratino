package ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

interface AssignmentR2dbcRepo<
    A : Assignment,
    Row : AssignmentPostgreRow<A>
    > : ReactiveCrudRepository<Row, UUID> {

    @Query("SELECT * FROM #{#tableName} WHERE state = :state")
    fun findByState(@Param("state") state: AssignmentState): Flux<Row>
}