package ru.pashkovske.buratino.common.utils

import com.google.protobuf.Timestamp
import java.time.Instant

object TimeUtils {
    fun tsToInstant(ts: Timestamp): Instant {
        return Instant.ofEpochSecond(ts.seconds, ts.nanos.toLong())
    }
}