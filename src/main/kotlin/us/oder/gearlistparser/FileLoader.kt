package us.oder.gearlistparser

class FileLoader {

    fun loadFile(name: String): String {
        val file = FileLoader::class.java.getResource("/$name") ?: return "null"
        return file.readText()
    }

    fun filterGearLines(input: List<String>): List<String> {
        return input.filter { it -> it.contains(" Mk ") }
    }
}
