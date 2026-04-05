package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling

object TopPriceAssignmentMapper {

    fun toDto(
        assignment: TopPriceAssignment,
        refreshNotifier: AssignmentScheduling?
    ): TopPriceAssignmentDto {
        return TopPriceAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshAssignmentScheduling = AssignmentSchedulingMapper.toDto(refreshNotifier),
            direction = assignment.direction,
            info = OrderInfoMapper.toDto(assignment.info),
            oneStepOver = assignment.oneStepOver
        )
    }
}