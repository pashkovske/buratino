package ru.pashkovske.buratino.tinkoff.service.common.utils;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public class TimeUtils {
    public static Instant map(Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
