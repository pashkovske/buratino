package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.repo

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepoInMemory
import ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model.ContinuousSpreadFractionAssignment

@Repository
class ContinuousSpreadFractionAssignmentRepo: AssignmentRepoInMemory<ContinuousSpreadFractionAssignment>()