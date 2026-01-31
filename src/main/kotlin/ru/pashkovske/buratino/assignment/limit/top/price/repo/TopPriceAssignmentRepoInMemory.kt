package ru.pashkovske.buratino.assignment.limit.top.price.repo

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.base.repo.inmemory.AssignmentRepoInMemory

@Repository
class TopPriceAssignmentRepoInMemory : AssignmentRepoInMemory<TopPriceAssignment>() {
}