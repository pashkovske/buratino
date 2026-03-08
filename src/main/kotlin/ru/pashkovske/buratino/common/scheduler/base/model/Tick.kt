package ru.pashkovske.buratino.common.scheduler.base.model

import java.time.Instant
import java.util.UUID

data class Tick(
    val id: UUID,
    val time: Instant
)