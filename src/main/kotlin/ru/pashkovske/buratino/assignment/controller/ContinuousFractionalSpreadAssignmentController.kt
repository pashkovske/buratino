package ru.pashkovske.buratino.assignment.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.dto.start.StartFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.mapper.ContinuousFractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.service.facade.ContinuousFractionalSpreadAssignmentExe
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/continuous/fractional-spread")
class ContinuousFractionalSpreadAssignmentController(
    dao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    exe: ContinuousFractionalSpreadAssignmentExe
) : ContinuousAssignmentController<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentStartCmd,
    ContinuousFractionalSpreadAssignmentDto,
    FractionalSpreadAssignmentDto
    >(
    dao = dao,
    exe = exe
) {
    override fun toDto(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignmentDto {
        return ContinuousFractionalSpreadAssignmentMapper.toDto(assignment)
    }

    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpreadAssignmentDto
    ): ContinuousFractionalSpreadAssignmentDto {
        val cmd = ContinuousFractionalSpreadAssignmentStartCmd(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            rate = body.rate,
            continueAssignmentSchedulingProperties = body.continueSchedulingPeriod?.let {
                PeriodicAssignmentSchedulingProperties(
                    period = it
                )
            },
            refreshAssignmentSchedulingProperties = body.refreshSchedulingPeriod?.let {
                PeriodicAssignmentSchedulingProperties(
                    period = it
                )
            }
        )
        return doStart(cmd)
    }
}