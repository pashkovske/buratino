package ru.pashkovske.buratino.assignment.parent.continuous.base.controller

import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.pashkovske.buratino.assignment.base.controller.BasicAssignmentController
import ru.pashkovske.buratino.assignment.base.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.controller.dto.ContinuousAssignmentDto
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.ContinuousAssignmentExe
import java.util.UUID

abstract class ContinuousAssignmentController<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ContinuousDto : ContinuousAssignmentDto<ContinuousA, ChildA, ChildDto>,
    ChildDto : BasicAssignmentDto<ChildA>
    >(
    dao: AssignmentDao<ContinuousA>,
    override val exe: ContinuousAssignmentExe<ContinuousA, ChildA, ContinuousCmd>
) : BasicAssignmentController<
    ContinuousA,
    ContinuousDto,
    ContinuousCmd
    >(
    dao = dao,
    exe = exe
) {

    @PatchMapping("/{id}/continue")
    fun continueAssignment(@PathVariable id: UUID): ContinuousDto {
        return toDto(exe.continueAssignment(id))
    }
}
