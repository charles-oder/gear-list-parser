package us.oder.gearlistparser

import java.lang.Error

class GearListParser {

    fun getGearList(fileContents: String): String {
        var output = "Needed, Name, Location, Energy\n"
        for (value in getGearRequirements(fileContents)) {
            output += "${value.count}, ${value.name}, ${value.location}, ${value.energyRequired}\n"
        }
        return output

    }

    private fun getFileLines(fileContents: String): List<String> {
        return fileContents.lines()
    }

    private fun getGearLines(fileContents: String): List<String> {
        val lines = getFileLines(fileContents)
        val rawLines = lines.filter { it -> it.toLowerCase().contains(" mk ")
                || it.toLowerCase().contains("raid only")
                || it.toLowerCase().contains("dark ")
                || it.toLowerCase().contains("light ")
                || it.toLowerCase().contains("agi ")
                || it.toLowerCase().contains("str ")
                || it.toLowerCase().contains("tac ")}
        return rawLines
    }

    private fun getGearRequirementsMap(fileContents: String): Map<String, GearRequirement> {
        val map: MutableMap<String, GearRequirement> = mutableMapOf()
        var workingName = ""
        val lines = getGearLines(fileContents)
        for ((index, line) in lines.withIndex()) {
            if (index % 2 == 0) {
                val set = line.split("x ")
                val count = set.first().toIntOrNull() ?: 0
                workingName = set.last()
                val previousEntry = map[workingName] ?: GearRequirement(workingName)
                previousEntry.count += count
                if (workingName != "") {
                    map[workingName] = previousEntry
                }
            } else {
                val previousEntry = map[workingName] ?: GearRequirement(workingName)
                if (line.contains("Raid Only")
                    || line.contains("AGI ")
                    || line.contains("STR ")
                    || line.contains("TAC ")) {
                    previousEntry.location = line.replace(", ", "-")
                } else {
                    val tokens = line.split(" ")
                    previousEntry.location = tokens[0] + " " + tokens[1] + " " + tokens[2]
                    var energyString =  tokens[3].removePrefix("(")
                    previousEntry.energyRequired = energyString.toInt()

                }
                if (workingName != "") {
                    map[workingName] = previousEntry
                }
            }
        }
        return map
    }

    private fun getGearRequirements(fileContents: String): List<GearRequirement> {
        val map = getGearRequirementsMap(fileContents)
        val reqList: List<GearRequirement> = map.map { it -> it.value }
        return reqList.sortedBy { it -> it.count }.reversed()
    }
}

class GearRequirement(val name: String) {
    var count: Int = 0
    var location: String = ""
    var energyRequired: Int = 0
}
