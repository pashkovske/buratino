package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier

object TopPriceAssignmentMapper {

    fun toDto(
        assignment: TopPriceAssignment,
        refreshNotifier: AssignmentNotifier?
    ): TopPriceAssignmentDto {
        return TopPriceAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshNotifier = AssignmentNotifierMapper.toDto(refreshNotifier),
            direction = assignment.direction,
            info = OrderInfoMapper.toDto(assignment.info),
            oneStepOver = assignment.oneStepOver
        )
    }
}