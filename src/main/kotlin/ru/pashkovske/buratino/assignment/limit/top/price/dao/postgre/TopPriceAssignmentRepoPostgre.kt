package ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface TopPriceAssignmentRepoPostgre : ReactiveCrudRepository<TopPriceAssignmentRow, UUID>
