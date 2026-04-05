package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier

object FractionalSpreadAssignmentMapper {

    fun toDto(
        assignment: FractionalSpreadAssignment,
        refreshNotifier: AssignmentNotifier?
    ): FractionalSpreadAssignmentDto {
        return FractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshNotifier = AssignmentNotifierMapper.toDto(refreshNotifier),
            direction = assignment.direction,
            info = OrderInfoMapper.toDto(assignment.info),
            rate = assignment.rate
        )
    }
}