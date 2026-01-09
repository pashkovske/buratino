package ru.pashkovske.buratino.flow.exe.action

import ru.pashkovske.buratino.flow.exe.context.ExeCtx

interface ActionExe<T> {

    fun execute(ctx: ExeCtx<T>)
    val name: String
}