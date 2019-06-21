package us.oder.gearlistparser

class GearListParser {

    fun getGearList(fileContents: String): String {
        var output = "Needed, Name\n"
        for (value in getGearRequirements(fileContents)) {
            output += "${value.count}, ${value.name}\n"
        }
        return output

    }

    private fun getFileLines(fileContents: String): List<String> {
        return fileContents.lines()
    }

    private fun getGearLines(fileContents: String): List<String> {
        val lines = getFileLines(fileContents)
        val rawLines = lines.filter { it -> it.toLowerCase().contains(" mk ")}
        return rawLines
    }

    private fun getGearRequirementsMap(fileContents: String): Map<String, GearRequirement> {
        val map: MutableMap<String, GearRequirement> = mutableMapOf()

        val lines = getGearLines(fileContents)
        for (line in lines) {
            val set = line.split("x ")
            val count = set.first().toIntOrNull() ?: 0
            val name = set.last()
            val previousEntry = map[name] ?: GearRequirement(name)
            previousEntry.count += count
            if (name != "") {
                map[name] = previousEntry
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
}
