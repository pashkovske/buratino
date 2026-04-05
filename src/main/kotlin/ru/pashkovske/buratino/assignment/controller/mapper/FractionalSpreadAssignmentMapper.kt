package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling

object FractionalSpreadAssignmentMapper {

    fun toDto(
        assignment: FractionalSpreadAssignment,
        refreshNotifier: AssignmentScheduling?
    ): FractionalSpreadAssignmentDto {
        return FractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshAssignmentScheduling = AssignmentSchedulingMapper.toDto(refreshNotifier),
            direction = assignment.direction,
            info = OrderInfoMapper.toDto(assignment.info),
            rate = assignment.rate
        )
    }
}