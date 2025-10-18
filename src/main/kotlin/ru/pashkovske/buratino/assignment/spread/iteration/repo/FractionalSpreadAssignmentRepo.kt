package ru.pashkovske.buratino.assignment.spread.iteration.repo

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.repo.AssignmentRepoInMemory
import ru.pashkovske.buratino.assignment.spread.iteration.model.FractionalSpreadAssignment

@Repository
class FractionalSpreadAssignmentRepo: AssignmentRepoInMemory<FractionalSpreadAssignment>() {
}