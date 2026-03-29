package ru.pashkovske.buratino.assignment.service.limit.order.price

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.BasicLimitOrderFactory
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.service.MarketPriceService

@Service
class TopPriceAssignmentLimitOrderFactory(
    private val marketPriceService: MarketPriceService
) : BasicLimitOrderFactory<TopPriceAssignment>() {

    override fun getPrice(assignment: TopPriceAssignment): Price {
        return if (assignment.oneStepOver) {
            marketPriceService.getOneStepOverTopOfBook(
                iid = assignment.iid,
                direction = assignment.direction
            )!!
        } else {
            marketPriceService.getTopOfBook(
                iid = assignment.iid,
                direction = assignment.direction
            )!!
        }
    }
}