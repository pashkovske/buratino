package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepoInMemory
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

@Repository
class FractionalSpreadAssignmentRepo: AssignmentRepoInMemory<FractionalSpreadAssignment>()