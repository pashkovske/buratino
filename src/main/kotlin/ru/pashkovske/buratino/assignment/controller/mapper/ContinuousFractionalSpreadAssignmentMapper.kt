package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling

object ContinuousFractionalSpreadAssignmentMapper {

    fun toDto(
        assignment: ContinuousFractionalSpreadAssignment,
        refreshNotifier: AssignmentScheduling?,
        continueNotifier: AssignmentScheduling?
    ): ContinuousFractionalSpreadAssignmentDto {
        return ContinuousFractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshAssignmentScheduling = AssignmentSchedulingMapper.toDto(refreshNotifier),
            child = FractionalSpreadAssignmentMapper.toDto(
                assignment.child,
                refreshNotifier = null
            ),
            continueAssignmentScheduling = AssignmentSchedulingMapper.toDto(continueNotifier)
        )
    }
}