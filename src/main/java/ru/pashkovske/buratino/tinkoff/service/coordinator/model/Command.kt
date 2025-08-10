package ru.pashkovske.buratino.tinkoff.service.coordinator.model

import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId

data class Command(
    val instrumentId: InstrumentId,
    val type: CommandType
)
