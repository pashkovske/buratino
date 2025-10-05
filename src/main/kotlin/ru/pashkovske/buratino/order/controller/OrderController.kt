package ru.pashkovske.buratino.order.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.repo.OrderRepo
import ru.pashkovske.buratino.order.service.OrderService

@Suppress("unused")
@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val orderRepo: OrderRepo
) {
    @GetMapping("/")
    fun getAll(): List<Order> {
        return orderRepo.getAll()
    }

    @GetMapping("/{id}")
    fun get(id: String): Order {
        return orderRepo.get(id)
    }
}