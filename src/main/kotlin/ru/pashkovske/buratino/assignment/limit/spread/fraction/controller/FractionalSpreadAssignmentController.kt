package ru.pashkovske.buratino.assignment.limit.spread.fraction.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.ExeChain
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.limit.top.price.controller.dto.StartFractionalSpredAssignmentDto
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/fractional-spread")
class FractionalSpreadAssignmentController(
    repo: AssignmentRepo<FractionalSpreadAssignment>,
    exe: FractionalSpreadAssignmentExe,
    chain: ExeChain
): BasicAssignmentController<FractionalSpreadAssignment>(
    repo = repo,
    exe = exe,
    chain = chain
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpredAssignmentDto
    ): FractionalSpreadAssignment {
        val assignment = FractionalSpreadAssignment(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            rate = body.rate
        )
        return doStart(assignment)
    }
}
