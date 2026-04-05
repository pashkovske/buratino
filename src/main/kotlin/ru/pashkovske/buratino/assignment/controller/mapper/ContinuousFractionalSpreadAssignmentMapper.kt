package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier

object ContinuousFractionalSpreadAssignmentMapper {

    fun toDto(
        assignment: ContinuousFractionalSpreadAssignment,
        refreshNotifier: AssignmentNotifier?,
        continueNotifier: AssignmentNotifier?
    ): ContinuousFractionalSpreadAssignmentDto {
        return ContinuousFractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshNotifier = AssignmentNotifierMapper.toDto(refreshNotifier),
            child = FractionalSpreadAssignmentMapper.toDto(
                assignment.child,
                refreshNotifier = null
            ),
            continueNotifier = AssignmentNotifierMapper.toDto(continueNotifier)
        )
    }
}