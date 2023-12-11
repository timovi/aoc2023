import kotlin.math.abs
import kotlin.system.measureTimeMillis

data class Coordinate(val x: Int, val y: Int)
data class MapAndGalaxies(val map: List<List<Char>>, val galaxies: List<Coordinate>)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val universe = expand(input.split("\r\n").map { it.toList() })

        val pathLengths = allDistances(universe.galaxies)
        println("Puzzle 1: ${pathLengths.sum()}") // 9591768
        println("Puzzle 2: ")
    }

    println("Duration: $duration ms")
}

fun expand(map: List<List<Char>>): MapAndGalaxies {
    val newMap = mutableListOf<List<Char>>()
    val galaxyCoordinates = mutableListOf<Coordinate>()

    for (y in map.indices) {
        newMap.add(map[y])
        if (!map[y].contains('#')) {
            newMap.add(map[y])
        }
    }

    val columnsWithoutGalaxies = mutableListOf<Int>()
    for (x in newMap[0].indices) {
        var containsGalaxy = false
        for (y in newMap.indices) {
            if (newMap[y][x] == '#') {
                containsGalaxy = true
                break
            }
        }
        if (!containsGalaxy) {
            columnsWithoutGalaxies.add(x)
        }
    }

    for (y in newMap.indices) {
        var newRow = ""
        for (x in newMap[y].indices) {
            newRow += newMap[y][x]
            if (columnsWithoutGalaxies.contains(x)) {
                newRow += '.'
            }
        }
        newMap[y] = newRow.toList()
    }

    for (y in newMap.indices) {
        for (x in newMap[y].indices) {
            if (newMap[y][x] == '#') {
                galaxyCoordinates.add(Coordinate(x, y))
            }
        }
    }

    return MapAndGalaxies(newMap, galaxyCoordinates)
}

fun allDistances(galaxies: List<Coordinate>): List<Int> {
    val distances = mutableListOf<Int>()
    for (i in galaxies.indices) {
        for (j in i + 1..<galaxies.size) {
            distances += manhattanDistance(galaxies[i], galaxies[j])
        }
    }
    return distances
}

fun manhattanDistance(from: Coordinate, to: Coordinate) =
    abs(from.x - to.x) + abs(from.y - to.y)
