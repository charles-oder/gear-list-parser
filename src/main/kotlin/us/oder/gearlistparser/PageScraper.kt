package us.oder.gearlistparser

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.jsoup.Jsoup
import java.io.File

fun main(args: Array<String>) {

    val allNodes: MutableList<MissionNode> = mutableListOf()

    File("C:\\Users\\charl\\Desktop\\SWGOH-LOOT\\").walk().forEach {
        val fileName = it.absolutePath
        if (fileName.contains(".html")) {
            println("Parsing $fileName")
            val nodes = nodeLoot(fileName)
            allNodes.addAll(nodes)
        }
    }

    val payload = MissionNodePayload(allNodes)
    println(Gson().toJson(payload))
    val locations = gearLocations(payload)
    println(Gson().toJson(locations))
    val gearLocationsCsvFile = File("C:\\Users\\charl\\Desktop\\gear-locations.csv")
    gearLocationsCsvFile.writeText(gearLocationsCsv(payload))
    println(gearLocationsCsv(payload))
}

private fun gearLocationsCsv(missionNodes: MissionNodePayload): String {
    val gearLocations = gearLocations(missionNodes)
    var output = "name, cost, locations\n"
    for (item in gearLocations.gearList) {
        val name = item.name
        val cost = item.cheapestLocations.firstOrNull()?.cost ?: 0
        val locationNames = item.cheapestLocations.map { it.name }
        val combined = locationNames.joinToString(" | ")
        output += "$name, $cost, $combined\n"
    }
    return output
}

private fun gearLocations(missionNodes: MissionNodePayload): GearLocationPayload {
    val map: MutableMap<String, Gear> = mutableMapOf()
    for (node in missionNodes.nodes) {
        for (item in node.items) {
            val existingEntry = map[item.name] ?: Gear(item.name)
            existingEntry.addLocation(GearLocation(node.name, node.nodeCost))
            map[item.name] = existingEntry
        }
    }
    return GearLocationPayload(map.map { it.value })
}

class Gear(val name: String) {
    var locations: MutableList<GearLocation> = mutableListOf()
    var cheapestLocations: List<GearLocation> = mutableListOf()

    fun addLocation(location: GearLocation) {
        locations.add(location)
        val cheapestLocation = locations.sortedBy { it.cost }.firstOrNull()
        cheapestLocations = locations.filter { it.cost == cheapestLocation?.cost }
    }
}

class GearLocation(val name: String, val cost: Int)

class GearLocationPayload(val gearList: List<Gear>)

private fun nodeLoot(fileName: String): List<MissionNode> {
    val fileNameComponents = fileName.split("\\").lastOrNull()?.split("-")
    val missionGroupName = "${fileNameComponents?.getOrNull(0) ?: ""} ${fileNameComponents?.getOrNull(1) ?: ""}"
    val doc = Jsoup.parse(FileLoader().loadFile(fileName))
    println(doc.title())
    val elements = doc.getElementsByClass("media-body-text")
    val nodes: MutableList<MissionNode> = mutableListOf()
    for (element in elements) {
        val node = MissionNode("$missionGroupName ${element.getElementsByTag("h4").first().text()}")
        val loot = element.getElementsByClass("loot-gear")
        for (item in loot) {
            val lootText = item.attr("title")
            val count = lootText.split("x M").firstOrNull()?.toIntOrNull() ?: 0
            val name = "M" + lootText.split("x M").lastOrNull()
            val lootItem = LootItem(count, name)
            node.items.add(lootItem)
        }
        println("Extracted Node: ${Gson().toJson(nodes)}")

        nodes.add(node)
    }
    return nodes
}


class MissionNodePayload(val nodes: List<MissionNode>)

class MissionNode(val name: String) {

    val nodeCost: Int
    val items: MutableList<LootItem> = mutableListOf()

    init {
        var cost = 0
        if (name.toLowerCase().contains("dark side") || name.toLowerCase().contains("light side")) {
            if (name.contains("1") || name.contains("2") || name.contains("3") || name.contains("4")) {
                cost = 6
            } else if (name.contains("5") || name.contains("6")) {
                cost = 8
            } else {
                cost = 10
            }
        } else if (name.toLowerCase().contains("fleet mission")) {
            cost = if (name.contains("1") ) {
                8
            } else {
                10
            }
        }
        if (name.contains("Hard")) {
            cost *= 2
        }
        this.nodeCost = cost
    }
}

class LootItem(val count: Int, val name: String)
