package ru.pashkovske.buratino.order.dao.postgre

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrderRepoPostgre : ReactiveCrudRepository<OrderRow, String>
