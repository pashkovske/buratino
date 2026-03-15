package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.controller.mapper

import ru.pashkovske.buratino.assignment.base.controller.mapper.AssignmentSchedulingMapper
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.mapper.FractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

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
