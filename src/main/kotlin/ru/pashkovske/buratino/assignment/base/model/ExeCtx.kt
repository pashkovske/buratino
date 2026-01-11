package ru.pashkovske.buratino.assignment.base.model

class ExeCtx<A : Assignment>(
    val assignment: A
) {

    private var isMutated: Boolean = false
    fun isMutated(): Boolean = isMutated
    fun setMutated() {
        isMutated = true
    }

    private var shouldSkip: Boolean = false
    fun shouldSkip(): Boolean = isMutated
    fun setShouldSkip() {
        shouldSkip = true
    }
}
