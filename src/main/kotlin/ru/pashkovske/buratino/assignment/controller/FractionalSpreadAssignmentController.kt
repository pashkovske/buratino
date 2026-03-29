package ru.pashkovske.buratino.assignment.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.dto.start.StartFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.mapper.FractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.service.facade.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/fractional-spread")
class FractionalSpreadAssignmentController(
    dao: AssignmentDao<FractionalSpreadAssignment>,
    exe: FractionalSpreadAssignmentExe
): BasicAssignmentController<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentDto,
    FractionalSpreadAssignmentStartCmd
    >(
    dao = dao,
    exe = exe
) {
    override fun toDto(assignment: FractionalSpreadAssignment): FractionalSpreadAssignmentDto {
        return FractionalSpreadAssignmentMapper.toDto(assignment)
    }

    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpreadAssignmentDto
    ): FractionalSpreadAssignmentDto {
        val cmd = FractionalSpreadAssignmentStartCmd(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            rate = body.rate,
            refreshAssignmentSchedulingProperties = body.refreshSchedulingPeriod?.let {
                PeriodicAssignmentSchedulingProperties(
                    period = it
                )
            }
        )
        return doStart(cmd)
    }
}