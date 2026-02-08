package ru.pashkovske.buratino.order.dao.postgre

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ru.pashkovske.buratino.order.model.OrderState

interface OrderRepoPostgre : ReactiveCrudRepository<OrderRow, String> {

    @Query("SELECT * FROM #{#tableName} WHERE current_state = :orderState")
    fun findByStatus(@Param("orderState") orderState: OrderState): Flux<OrderRow>
}
