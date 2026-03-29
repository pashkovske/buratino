package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment

object ContinuousFractionalSpreadAssignmentMapper {

    fun toDto(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignmentDto {
        return ContinuousFractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshAssignmentSchedulingProperties = AssignmentSchedulingMapper.toDto(assignment.refreshAssignmentSchedulingProperties),
            refreshAssignmentScheduling = AssignmentSchedulingMapper.toDto(assignment.getRefreshAssignmentScheduling()),
            child = FractionalSpreadAssignmentMapper.toDto(assignment.child),
            continueAssignmentSchedulingProperties = AssignmentSchedulingMapper.toDto(assignment.continueAssignmentSchedulingProperties),
            continueAssignmentScheduling = AssignmentSchedulingMapper.toDto(assignment.getContinueAssignmentScheduling())
        )
    }
}