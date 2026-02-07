package ru.pashkovske.buratino.order.repo.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.repo.OrderRepo

@Repository
class OrderDaoPostgre(
    private val orderRepoPostgre: OrderRepoPostgre,
    private val mapper: OrderMapper,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : OrderRepo {

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
