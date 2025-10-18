package ru.pashkovske.buratino.assignment.limit.spread.fraction.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.InstrumentAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExecutor
import ru.pashkovske.buratino.assignment.limit.top.price.controller.dto.StartFractionalSpredAssignmentDto
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

@Suppress("unused")
@RestController
@RequestMapping("/assignment/fractional-spread")
class FractionalSpreadAssignmentController(
    val assignmentRepo: AssignmentRepo<FractionalSpreadAssignment>,
    val assignmentExecutor: FractionalSpreadAssignmentExecutor
) {
    @PostMapping("/{instrumentId}/start/{direction}")
    fun start(
        @PathVariable instrumentId: String,
        @PathVariable direction: String,
        @RequestBody body: StartFractionalSpredAssignmentDto
    ): FractionalSpreadAssignment {
        val assignment = FractionalSpreadAssignment(
            iid = InstrumentId(id = instrumentId),
            direction = OrderDirection.fromString(direction),
            rate = body.rate
        )
        assignmentExecutor.start(assignment)
        return assignment
    }

    @PatchMapping("/{id}/refresh")
    fun refresh(@PathVariable id: UUID): FractionalSpreadAssignment {
        assignmentExecutor.refresh(id)
        return assignmentRepo.get(id)
    }

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: UUID): FractionalSpreadAssignment {
        assignmentExecutor.cancel(id)
        return assignmentRepo.get(id)
    }

    @GetMapping("/")
    fun getAll(): List<FractionalSpreadAssignment> {
        return assignmentRepo.getAll()
    }

    @PatchMapping("/refresh-all")
    fun refreshAll(): List<FractionalSpreadAssignment> {
        val activeAssignments: List<FractionalSpreadAssignment> = assignmentRepo.getAll()
            .filter { it.status == AssignmentStatus.IN_PROGRESS }
        activeAssignments
            .map(InstrumentAssignment::id)
            .forEach(assignmentExecutor::refresh)
        return activeAssignments
    }
}