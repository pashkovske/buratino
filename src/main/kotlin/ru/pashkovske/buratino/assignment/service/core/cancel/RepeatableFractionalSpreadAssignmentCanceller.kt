package ru.pashkovske.buratino.assignment.service.core.cancel

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment

@Service
class RepeatableFractionalSpreadAssignmentCanceller(
    assignmentDao: AssignmentDao<RepeatableFractionalSpreadAssignment>,
    childAssignmentCanceller: FractionalSpreadAssignmentCanceller
) : RepeatableAssignmentCanceller<FractionalSpreadAssignment, RepeatableFractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    childAssignmentCanceller = childAssignmentCanceller,
)
