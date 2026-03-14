package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.dao.postgre

import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

interface ContinuousFractionalSpreadAssignmentRepoPostgre : AssignmentR2dbcRepo<
    ContinuousFractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentRow
    >
