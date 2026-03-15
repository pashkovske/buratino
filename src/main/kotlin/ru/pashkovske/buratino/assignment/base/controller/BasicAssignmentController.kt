package ru.pashkovske.buratino.assignment.base.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.pashkovske.buratino.assignment.base.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import java.util.UUID

@Suppress("unused")
abstract class BasicAssignmentController<
    A : Assignment,
    Dto : BasicAssignmentDto<A>,
    Cmd : AssignmentStartCmd<A>
    >(
    open val dao: AssignmentDao<A>,
    open val exe: AssignmentExe<A, Cmd>
) {
    protected abstract fun toDto(assignment: A): Dto

    protected fun doStart(cmd: Cmd): Dto {
        return toDto(exe.start(cmd))
    }

    @PatchMapping("/{id}/refresh")
    fun refresh(@PathVariable id: UUID): Dto {
        return toDto(exe.refresh(id))
    }

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: UUID): Dto {
        return toDto(exe.cancel(id))
    }

    @GetMapping("/")
    fun getAll(): List<Dto> {
        return dao.getAll().map(::toDto)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): Dto {
        return toDto(dao.get(id))
    }

    @PatchMapping("/refresh-all")
    fun refreshAll(): List<Dto> {
        val activeAssignments: List<A> = dao.getAll()
            .filter { it.state == AssignmentState.IN_PROGRESS }
        activeAssignments
            .map(Assignment::id)
            .forEach(exe::refresh)
        return activeAssignments.map(::toDto)
    }
}
