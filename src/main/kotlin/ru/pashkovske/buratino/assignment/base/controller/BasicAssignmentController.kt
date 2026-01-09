package ru.pashkovske.buratino.assignment.base.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.exe.AssignmentExe
import java.util.UUID

@Suppress("unused")
abstract class BasicAssignmentController<A: Assignment>(
    open val repo: AssignmentRepo<A>,
    open val exe: AssignmentExe<A>
) {
    protected fun doStart(assignment: A): A {
        return exe.start(assignment)
    }

    @PatchMapping("/{id}/refresh")
    fun refresh(@PathVariable id: UUID): A {
        return exe.refresh(id)
    }

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: UUID): A {
        return exe.cancel(id)
    }

    @GetMapping("/")
    fun getAll(): List<A> {
        return repo.getAll()
    }

    @PatchMapping("/refresh-all")
    fun refreshAll(): List<A> {
        val activeAssignments: List<A> = repo.getAll()
            .filter { it.status == AssignmentStatus.IN_PROGRESS }
        activeAssignments
            .map(Assignment::id)
            .forEach(exe::refresh)
        return activeAssignments
    }
}
