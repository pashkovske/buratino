package ru.pashkovske.buratino.tinkoff.util;

import com.google.gson.Gson;

public class Deserializer {
    public static <T> T deserialize(String path, Class<T> cls) {
        return new Gson().fromJson(FileLoader.loadJson(path), cls);
    }
}
