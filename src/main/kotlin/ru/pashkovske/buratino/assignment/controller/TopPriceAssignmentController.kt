package ru.pashkovske.buratino.assignment.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.controller.dto.BasicStartAssignmentDto
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.controller.mapper.TopPriceAssignmentMapper
import ru.pashkovske.buratino.assignment.service.facade.TopPriceAssignmentExe
import ru.pashkovske.buratino.assignment.model.cmd.TopPriceAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicNotifierProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

@Suppress("unused")
@RestController
@RequestMapping("/assignment/top-price")
class TopPriceAssignmentController(
    dao: AssignmentDao<TopPriceAssignment>,
    exe: TopPriceAssignmentExe,
    refreshNotifierDao: RefreshNotifierDao
): BasicAssignmentController<TopPriceAssignment, TopPriceAssignmentDto, TopPriceAssignmentStartCmd>(
    dao = dao,
    exe = exe,
    refreshNotifierDao = refreshNotifierDao
) {
    override fun toDto(assignment: TopPriceAssignment): TopPriceAssignmentDto {
        return TopPriceAssignmentMapper.toDto(
            assignment = assignment,
            refreshNotifier = getRefreshNotifier(assignment)
        )
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
            refreshNotifierProperties = body.refreshNotifyPeriod?.let {
                PeriodicNotifierProperties(
                    period = it
                )
            }
        )
        return doStart(cmd)
    }
}