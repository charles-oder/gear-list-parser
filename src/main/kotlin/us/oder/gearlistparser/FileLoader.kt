package us.oder.gearlistparser

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class FileLoader {

    fun loadFile(name: String): String {
        //val file = FileLoader::class.java.getResource("/$name") ?: return "null"
        val file = File(name)
        return file.readText()
    }

    fun filterGearLines(input: List<String>): List<String> {
        return input.filter { it -> it.contains(" Mk ") }
    }
}
