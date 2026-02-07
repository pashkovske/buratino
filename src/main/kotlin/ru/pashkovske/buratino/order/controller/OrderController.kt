package ru.pashkovske.buratino.order.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.service.OrderService

@Suppress("unused")
@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val orderDao: OrderDao
) {
    @GetMapping("/")
    fun getAll(): List<Order> {
        return orderDao.getAll()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Order {
        return orderDao.get(id)
    }
}