package ru.pashkovske.buratino.assignment.top.price.repo

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.repo.AssignmentRepoInMemory
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment

@Repository
class TopPriceAssignmentRepoInMemory : AssignmentRepoInMemory<TopPriceAssignment>() {
}