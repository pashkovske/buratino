package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.RepeatableFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier

object RepeatableFractionalSpreadAssignmentMapper {

    fun toDto(
        assignment: RepeatableFractionalSpreadAssignment,
        refreshNotifier: AssignmentNotifier?
    ): RepeatableFractionalSpreadAssignmentDto {
        return RepeatableFractionalSpreadAssignmentDto(
            id = assignment.id,
            iid = assignment.iid,
            state = assignment.state,
            refreshNotifier = AssignmentNotifierMapper.toDto(refreshNotifier),
            child = FractionalSpreadAssignmentMapper.toDto(
                assignment.child,
                refreshNotifier = null
            )
        )
    }
}
