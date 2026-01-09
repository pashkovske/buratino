package ru.pashkovske.buratino.flow.exe.router

import ru.pashkovske.buratino.flow.exe.context.ExeCtxView

interface RouterExe<T> {

    fun execute(ctx: ExeCtxView<T>): Route
    val name: String
    val defaultRoute: Route
}
