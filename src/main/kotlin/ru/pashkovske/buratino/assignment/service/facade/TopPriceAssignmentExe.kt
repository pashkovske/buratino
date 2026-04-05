package ru.pashkovske.buratino.assignment.service.facade

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.TopPriceAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.core.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter

@Service
final class TopPriceAssignmentExe(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    assignmentRefresher: AssignmentRefresher<TopPriceAssignment>,
    assignmentCanceller: AssignmentCanceller<TopPriceAssignment>,
    assignmentStarter: AssignmentStarter<TopPriceAssignment>,
    assignmentBuilder: AssignmentBuilder<TopPriceAssignment, TopPriceAssignmentStartCmd>
) : LimitOrderAssignmentExe<
    TopPriceAssignment,
    TopPriceAssignmentStartCmd
    >(
    assignmentDao = assignmentDao,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)