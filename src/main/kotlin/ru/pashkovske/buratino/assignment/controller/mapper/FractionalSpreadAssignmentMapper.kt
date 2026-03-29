package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

object FractionalSpreadAssignmentMapper {

    fun toDto(assignment: FractionalSpreadAssignment): FractionalSpreadAssignmentDto {
        return FractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshAssignmentSchedulingProperties = AssignmentSchedulingMapper.toDto(assignment.refreshAssignmentSchedulingProperties),
            refreshAssignmentScheduling = AssignmentSchedulingMapper.toDto(assignment.getRefreshAssignmentScheduling()),
            direction = assignment.direction,
            info = OrderInfoMapper.toDto(assignment.info),
            rate = assignment.rate
        )
    }
}