package ru.pashkovske.buratino.assignment.top.price.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.top.price.service.TopPriceAssignmentExecutor
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

@Suppress("unused")
@RestController
@RequestMapping("/assignment/top-price")
class TopPriceAssignmentController(
    private val assignmentExecutor: TopPriceAssignmentExecutor,
    private val assignmentRepo: AssignmentRepo<TopPriceAssignment>
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestParam oneStepOver: Boolean?
    ): TopPriceAssignment {
        val assignment = TopPriceAssignment(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            oneStepOver = oneStepOver ?: false
        )
        assignmentExecutor.start(assignment)
        return assignment
    }

    @PatchMapping("/{id}/refresh")
    fun refresh(@PathVariable id: UUID): TopPriceAssignment {
        assignmentExecutor.refresh(id)
        return assignmentRepo.get(id)
    }

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: UUID): TopPriceAssignment {
        assignmentExecutor.cancel(id)
        return assignmentRepo.get(id)
    }

    @GetMapping("/")
    fun getAll(): List<TopPriceAssignment> {
        return assignmentRepo.getAll()
    }
}
