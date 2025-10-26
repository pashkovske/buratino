package ru.pashkovske.burationo.util

import com.google.gson.Gson
import java.io.FileReader
import java.io.FileNotFoundException
import com.google.gson.JsonSyntaxException

object FileLoader {
    fun <T> loadFromJson(
        path: String,
        clazz: Class<T>
    ): T {
        try {
            val gson = Gson()
            return gson.fromJson(FileReader(path), clazz)
        } catch (e: FileNotFoundException) {
            throw RuntimeException("File not found: $path", e)
        } catch (e: JsonSyntaxException) {
            throw RuntimeException("Invalid JSON syntax in file: $path", e)
        } catch (e: Exception) {
            throw RuntimeException("Error loading JSON from file: $path", e)
        }
    }
}
