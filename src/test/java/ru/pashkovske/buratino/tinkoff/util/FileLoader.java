package ru.pashkovske.buratino.tinkoff.util;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class FileLoader {
    private final String basePath;

    public String loadJson(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(basePath + path + ".json")));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
