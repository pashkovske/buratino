package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface FractionalSpreadAssignmentRepoPostgre : ReactiveCrudRepository<FractionalSpreadAssignmentRow, UUID>
