package ru.pashkovske.buratino.assignment.limit.top.price.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.controller.dto.BasicStartAssignmentDto
import ru.pashkovske.buratino.assignment.limit.top.price.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.limit.top.price.controller.mapper.TopPriceAssignmentMapper
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignmentStartCmd
import ru.pashkovske.buratino.assignment.limit.top.price.service.TopPriceAssignmentExe
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/top-price")
class TopPriceAssignmentController(
    dao: AssignmentDao<TopPriceAssignment>,
    exe: TopPriceAssignmentExe
): BasicAssignmentController<TopPriceAssignment, TopPriceAssignmentDto, TopPriceAssignmentStartCmd>(
    dao = dao,
    exe = exe
) {
    override fun toDto(assignment: TopPriceAssignment): TopPriceAssignmentDto {
        return TopPriceAssignmentMapper.toDto(assignment)
    }

    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestParam oneStepOver: Boolean?,
        @RequestBody body: BasicStartAssignmentDto
    ): TopPriceAssignmentDto {
        val cmd = TopPriceAssignmentStartCmd(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            oneStepOver = oneStepOver ?: false,
            refreshAssignmentSchedulingProperties = body.refreshSchedulingPeriod?.let {
                PeriodicAssignmentSchedulingProperties(
                    period = it
                )
            }
        )
        return doStart(cmd)
    }
}
