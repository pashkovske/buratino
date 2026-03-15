package ru.pashkovske.buratino.assignment.limit.top.price.controller.mapper

import ru.pashkovske.buratino.assignment.base.controller.mapper.AssignmentSchedulingMapper
import ru.pashkovske.buratino.assignment.limit.base.controller.mapper.OrderInfoMapper
import ru.pashkovske.buratino.assignment.limit.top.price.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment

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
