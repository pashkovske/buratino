package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.dao.postgre

import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

interface ContinuousFractionalSpreadAssignmentRepoPostgre : AssignmentR2dbcRepo<
    ContinuousFractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentRow
    >
