package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto.StartFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.controller.ContinuousAssignmentController
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.controller.mapper.ContinuousFractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.ContinuousFractionalSpreadAssignmentExe
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
