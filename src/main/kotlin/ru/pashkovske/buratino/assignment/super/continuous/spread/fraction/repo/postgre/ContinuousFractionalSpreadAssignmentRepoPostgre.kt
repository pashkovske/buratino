package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.repo.postgre

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface ContinuousFractionalSpreadAssignmentRepoPostgre : ReactiveCrudRepository<ContinuousFractionalSpreadAssignmentRow, UUID>
