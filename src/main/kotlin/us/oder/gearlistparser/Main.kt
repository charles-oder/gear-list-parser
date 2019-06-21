package us.oder.gearlistparser


fun main(args: Array<String>) {
    println("Hello World!!!!")
    val loader = FileLoader()
    val parser = GearListParser()
    val fileContents = loader.loadFile("RawData.txt")
    val gear = parser.getGearList(fileContents)
    println("lines: \n$gear")
}
