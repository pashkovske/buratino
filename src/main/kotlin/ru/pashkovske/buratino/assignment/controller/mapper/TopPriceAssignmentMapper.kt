package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment

object TopPriceAssignmentMapper {

    fun toDto(assignment: TopPriceAssignment): TopPriceAssignmentDto {
        return TopPriceAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshAssignmentSchedulingProperties = AssignmentSchedulingMapper.toDto(assignment.refreshAssignmentSchedulingProperties),
            refreshAssignmentScheduling = AssignmentSchedulingMapper.toDto(assignment.getRefreshAssignmentScheduling()),
            direction = assignment.direction,
            info = OrderInfoMapper.toDto(assignment.info),
            oneStepOver = assignment.oneStepOver
        )
    }
}