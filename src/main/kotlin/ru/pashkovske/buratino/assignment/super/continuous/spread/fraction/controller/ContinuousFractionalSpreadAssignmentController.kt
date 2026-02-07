package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.controller

import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.service.ContinuousFractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto.StartFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

@Suppress("unused")
@RestController
@RequestMapping("/assignment/continuous/fractional-spread")
class ContinuousFractionalSpreadAssignmentController(
    dao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    override val exe: ContinuousFractionalSpreadAssignmentExe
): BasicAssignmentController<ContinuousFractionalSpreadAssignment>(
    dao = dao,
    exe = exe
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpreadAssignmentDto
    ): ContinuousFractionalSpreadAssignment {
        val iid = InstrumentId(id = instrumentId)
        val nestedAssignment = FractionalSpreadAssignment.newAssignment(
            iid = iid,
            refreshSchedulingProperties = null,
            direction = OrderDirection.fromString(direction),
            rate = body.rate
        )
        val assignment = ContinuousFractionalSpreadAssignment.newAssignment(
            iid = iid,
            nested = nestedAssignment,
            refreshSchedulingProperties = body.refreshSchedulingInterval?.let {
                SchedulingProperties(
                    interval = it
                )
            },
            continueSchedulingProperties = body.continueSchedulingInterval?.let {
                SchedulingProperties(
                    interval = it
                )
            }
        )
        return exe.start(assignment)
    }

    @PatchMapping("/{id}/continue")
    fun continueAssignment(@PathVariable id: UUID): ContinuousFractionalSpreadAssignment {
        return exe.continueAssignment(id)
    }
}
