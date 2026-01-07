package ru.pashkovske.buratino.flow.exe.router

import ru.pashkovske.buratino.flow.exe.context.ExeCtx

interface RouterExe<Ctx: ExeCtx> {

    fun execute(ctx: Ctx): Route
    val name: String
    val defaultRoute: Route
}
