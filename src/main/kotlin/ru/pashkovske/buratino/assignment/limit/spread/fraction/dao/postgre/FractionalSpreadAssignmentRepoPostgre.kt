package ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface FractionalSpreadAssignmentRepoPostgre : ReactiveCrudRepository<FractionalSpreadAssignmentRow, UUID>
