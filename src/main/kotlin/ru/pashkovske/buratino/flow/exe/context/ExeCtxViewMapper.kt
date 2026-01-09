package ru.pashkovske.buratino.flow.exe.context

interface ExeCtxViewMapper<T> {

    fun map(exeCtx: ExeCtx<T>) : ExeCtxView<T>
}