package ru.pashkovske.buratino.assignment.limit.top.price.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.service.TopPriceAssignmentExe
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.ExeChain
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/top-price")
class TopPriceAssignmentController(
    repo: AssignmentRepo<TopPriceAssignment>,
    exe: TopPriceAssignmentExe,
    chain: ExeChain
): BasicAssignmentController<TopPriceAssignment>(
    repo = repo,
    exe = exe,
    chain = chain
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
        return doStart(assignment)
    }
}
