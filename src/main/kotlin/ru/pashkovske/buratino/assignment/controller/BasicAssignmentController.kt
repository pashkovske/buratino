package ru.pashkovske.buratino.assignment.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.pashkovske.buratino.assignment.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import java.util.UUID

@Suppress("unused")
abstract class BasicAssignmentController<
    A : Assignment,
    Dto : BasicAssignmentDto<A>,
    Cmd : AssignmentStartCmd<A>
    >(
    open val dao: AssignmentDao<A>,
    open val exe: AssignmentExe<A, Cmd>,
    private val refreshNotifierDao: RefreshNotifierDao
) {
    protected abstract fun toDto(assignment: A): Dto

    protected fun getRefreshNotifier(assignment: A): AssignmentNotifier? {
        val notifierId: UUID = assignment.getRefreshNotifierId() ?: return null
        return refreshNotifierDao.get(notifierId)
    }

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