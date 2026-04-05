package ru.pashkovske.buratino.assignment.controller

import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.pashkovske.buratino.assignment.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.controller.dto.ContinuousAssignmentDto
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.ContinueNotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.service.facade.ContinuousAssignmentExe
import java.util.UUID

abstract class ContinuousAssignmentController<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ContinuousDto : ContinuousAssignmentDto<ContinuousA, ChildA, ChildDto>,
    ChildDto : BasicAssignmentDto<ChildA>
    >(
    dao: AssignmentDao<ContinuousA>,
    override val exe: ContinuousAssignmentExe<ContinuousA, ChildA, ContinuousCmd>,
    refreshNotifierDao: RefreshNotifierDao,
    private val continueNotifierDao: ContinueNotifierDao
) : BasicAssignmentController<
    ContinuousA,
    ContinuousDto,
    ContinuousCmd
    >(
    dao = dao,
    exe = exe,
    refreshNotifierDao = refreshNotifierDao
) {

    protected fun getContinueNotifier(assignment: ContinuousA): AssignmentScheduling? {
        val notifierId: UUID = assignment.getContinueNotifierId() ?: return null
        return continueNotifierDao.get(notifierId)
    }

    @PatchMapping("/{id}/continue")
    fun continueAssignment(@PathVariable id: UUID): ContinuousDto {
        return toDto(
            assignment = exe.continueAssignment(id)
        )
    }
}