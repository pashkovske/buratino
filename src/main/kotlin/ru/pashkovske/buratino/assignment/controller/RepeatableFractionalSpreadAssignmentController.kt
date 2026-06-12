package ru.pashkovske.buratino.assignment.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.controller.dto.RepeatableFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.dto.start.StartFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.mapper.RepeatableFractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.cmd.RepeatableFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicNotifierProperties
import ru.pashkovske.buratino.assignment.service.facade.RepeatableFractionalSpreadAssignmentExe
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/repeatable/fractional-spread")
class RepeatableFractionalSpreadAssignmentController(
    dao: AssignmentDao<RepeatableFractionalSpreadAssignment>,
    exe: RepeatableFractionalSpreadAssignmentExe,
    refreshNotifierDao: RefreshNotifierDao
) : BasicAssignmentController<
    RepeatableFractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignmentDto,
    RepeatableFractionalSpreadAssignmentStartCmd
    >(
    dao = dao,
    exe = exe,
    refreshNotifierDao = refreshNotifierDao
) {
    override fun toDto(assignment: RepeatableFractionalSpreadAssignment): RepeatableFractionalSpreadAssignmentDto {
        return RepeatableFractionalSpreadAssignmentMapper.toDto(
            assignment = assignment,
            refreshNotifier = getRefreshNotifier(assignment)
        )
    }

    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpreadAssignmentDto
    ): RepeatableFractionalSpreadAssignmentDto {
        val cmd = RepeatableFractionalSpreadAssignmentStartCmd(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            rate = body.rate,
            refreshNotifierProperties = body.refreshNotifyPeriod?.let {
                PeriodicNotifierProperties(
                    period = it
                )
            }
        )
        return doStart(cmd)
    }
}
