package ru.pashkovske.buratino.assignment.base.model

class ExeCtx<A : Assignment>(
    val assignment: A
) {

    private var isMutated: Boolean = false

    fun isMutated(): Boolean = isMutated

    fun setMutated() {
        isMutated = true
    }
}
