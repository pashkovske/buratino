package ru.pashkovske.buratino.flow.exe.action

import ru.pashkovske.buratino.flow.exe.context.ExeCtx

interface ActionExe<Ctx: ExeCtx> {

    fun execute(ctx: Ctx)
    val name: String
}