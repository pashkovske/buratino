package ru.pashkovske.buratino.tinkoff.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLoader {
    private static final String basePath = "src/test/resources/";

    public static String loadJson(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(basePath + path + ".json")));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
