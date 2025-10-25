package ru.pashkovske.buratino.assignment.continuous.spread.fraction.controller

import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.ExeChain
import ru.pashkovske.buratino.assignment.continuous.spread.fraction.model.ContinuousSpreadFractionAssignment
import ru.pashkovske.buratino.assignment.continuous.spread.fraction.service.ContinuousSpreadFractionAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.controller.dto.StartFractionalSpredAssignmentDto
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

@Suppress("unused")
@RestController
@RequestMapping("/assignment/continuous/fractional-spread")
class ContinuousSpreadFractionAssignmentController(
    repo: AssignmentRepo<ContinuousSpreadFractionAssignment>,
    override val exe: ContinuousSpreadFractionAssignmentExe,
    chain: ExeChain
): BasicAssignmentController<ContinuousSpreadFractionAssignment>(
    repo = repo,
    exe = exe,
    chain = chain
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpredAssignmentDto
    ): ContinuousSpreadFractionAssignment {
        val iid = InstrumentId(id = instrumentId)
        val nestedAssignment = FractionalSpreadAssignment(
            iid = iid,
            direction = OrderDirection.fromString(direction),
            rate = body.rate
        )
        val assignment = ContinuousSpreadFractionAssignment(
            iid = iid,
            currentAssignment = nestedAssignment
        )
        return doStart(assignment)
    }

    @PatchMapping("/{id}/continue")
    fun continueAssignment(@PathVariable id: UUID): ContinuousSpreadFractionAssignment {
        return chain.continueAssignment(
            id = id,
            exe = exe
        )
    }
}