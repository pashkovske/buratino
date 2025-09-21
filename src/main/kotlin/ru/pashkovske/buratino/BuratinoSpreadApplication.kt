package ru.pashkovske.buratino

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = [
    "ru.pashkovske.buratino"
])
class BuratinoSpreadApplication

fun main(args: Array<String>) {
    runApplication<BuratinoSpreadApplication>(*args)
}
