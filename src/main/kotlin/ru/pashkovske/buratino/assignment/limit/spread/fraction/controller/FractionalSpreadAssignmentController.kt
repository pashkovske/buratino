package ru.pashkovske.buratino.assignment.limit.spread.fraction.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto.StartFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/fractional-spread")
class FractionalSpreadAssignmentController(
    dao: AssignmentDao<FractionalSpreadAssignment>,
    exe: FractionalSpreadAssignmentExe
): BasicAssignmentController<FractionalSpreadAssignment>(
    dao = dao,
    exe = exe
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpreadAssignmentDto
    ): FractionalSpreadAssignment {
        val assignment = FractionalSpreadAssignment.newAssignment(
            iid = InstrumentId(id = instrumentId),
            refreshSchedulingProperties = body.refreshSchedulingInterval?.let {
                SchedulingProperties(
                    interval = it
                )
            },
            direction = OrderDirection.fromString(direction),
            rate = body.rate
        )
        return doStart(assignment)
    }
}
