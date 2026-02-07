package ru.pashkovske.buratino.assignment.limit.top.price.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.controller.dto.BasicStartAssignmentDto
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.service.TopPriceAssignmentExe
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/top-price")
class TopPriceAssignmentController(
    dao: AssignmentDao<TopPriceAssignment>,
    exe: TopPriceAssignmentExe
): BasicAssignmentController<TopPriceAssignment>(
    dao = dao,
    exe = exe
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestParam oneStepOver: Boolean?,
        @RequestBody body: BasicStartAssignmentDto
    ): TopPriceAssignment {
        val assignment = TopPriceAssignment.newAssignment(
            iid = InstrumentId(id = instrumentId),
            refreshSchedulingProperties = body.refreshSchedulingInterval?.let {
                SchedulingProperties(
                    interval = it
                )
            },
            direction = OrderDirection.fromString(direction),
            oneStepOver = oneStepOver ?: false
        )
        return doStart(assignment)
    }
}
