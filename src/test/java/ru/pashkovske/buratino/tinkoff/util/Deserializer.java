package ru.pashkovske.buratino.tinkoff.util;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Deserializer {
    private final FileLoader fileLoader;

    public <T> T deserialize(String path, Class<T> cls) {
        return new Gson().fromJson(fileLoader.loadJson(path), cls);
    }
}
