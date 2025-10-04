package ru.pashkovske.buratino.assignment.top.price.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.top.price.service.TopPriceAssignmentExecutor
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/top-price")
class TopPriceAssignmentController(
    private val assignmentExecutor: TopPriceAssignmentExecutor
) {
    @PostMapping("/{instrumentId}/buy/start")
    fun buy(@PathVariable instrumentId: String): TopPriceAssignment {
        val assignment = TopPriceAssignment(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.BUY
        )
        assignmentExecutor.run(assignment)
        return assignment
    }

    @PostMapping("/{instrumentId}/sell/start")
    fun sell(@PathVariable instrumentId: String): TopPriceAssignment {
        val assignment = TopPriceAssignment(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.SELL
        )
        assignmentExecutor.run(assignment)
        return assignment}
}
