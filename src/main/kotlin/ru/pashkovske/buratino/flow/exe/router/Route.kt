package ru.pashkovske.buratino.flow.exe.router

interface Route {
    val allRoutes: Set<String>
    val value: String
}