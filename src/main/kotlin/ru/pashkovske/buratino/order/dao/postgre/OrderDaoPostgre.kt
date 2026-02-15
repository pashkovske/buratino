package ru.pashkovske.buratino.order.dao.postgre

import org.springframework.context.annotation.DependsOn
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderState

@Repository
@DependsOn("flywayInitializer")
class OrderDaoPostgre(
    private val orderRepoPostgre: OrderRepoPostgre,
    private val mapper: OrderMapper,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : OrderDao {

    override fun get(id: String): Order {
        return orderRepoPostgre.findById(id)
            .map(mapper::toOrder)
            .block() ?: throw IllegalArgumentException("Order with id $id not found")
    }

    override fun getAll(): List<Order> {
        return orderRepoPostgre.findAll()
            .map(mapper::toOrder)
            .collectList()
            .block() ?: emptyList()
    }

    override fun getByState(state: OrderState): Flux<Order> {
        return orderRepoPostgre.findByStatus(state)
            .map(mapper::toOrder)
    }

    override fun create(order: Order) {
        r2dbcEntityTemplate.insert(mapper.toOrderRow(order))
            .block()
    }

    override fun update(order: Order) {
        r2dbcEntityTemplate.update(mapper.toOrderRow(order))
            .block()
    }

    override fun deleteAll() {
        orderRepoPostgre.deleteAll()
            .block()
    }
}
