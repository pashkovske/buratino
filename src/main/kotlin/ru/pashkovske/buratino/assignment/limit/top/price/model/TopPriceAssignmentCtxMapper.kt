package ru.pashkovske.buratino.assignment.limit.top.price.model

object TopPriceAssignmentCtxMapper {

    fun map(assignment: TopPriceAssignment): TopPriceCtx {
        return TopPriceCtx(
            oneStepOver = assignment.oneStepOver,
            iid = assignment.iid,
            direction = assignment.direction
        )
    }
}