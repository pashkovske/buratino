package ru.pashkovske.buratino.order.repo.postgre

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrderRepoPostgre : ReactiveCrudRepository<OrderRow, String>
