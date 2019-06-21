package us.oder.gearlistparser

import java.io.File


fun main(args: Array<String>) {
    val source = args.getOrNull(0)
    if (source == null) {
        println("Usage: gearParser <source> [destination]")
        return
    }
    println("Parsing File: $source")
    val loader = FileLoader()
    val parser = GearListParser()
    val fileContents = loader.loadFile(source)
    val gear = parser.getGearList(fileContents)
    val destination = args.getOrNull(1) ?: "$source.csv"
    val newFile = File(destination)
    println("Writing File: $destination")
    newFile.writeText(gear)
    println("complete")
}
