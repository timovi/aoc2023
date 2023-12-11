import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

data class Coordinate(val x: Int, val y: Int)
data class Universe(val emptyRows: Set<Int>, val emptyCols: Set<Int>, val galaxies: List<Coordinate>)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val universe = universe(input.split("\r\n").map { it.toList() })

        println("Puzzle 1: ${distances(2, universe).sum()}")
        println("Puzzle 2: ${distances(1000000, universe).sum()}")
    }

    println("Duration: $duration ms")
}

fun universe(map: List<List<Char>>): Universe {
    val emptyRows = mutableSetOf<Int>()
    val emptyCols = mutableSetOf<Int>()
    val galaxyCoordinates = mutableListOf<Coordinate>()

    for (y in map.indices) {
        if (!map[y].contains('#')) {
            emptyRows.add(y)
        }
    }

    for (x in map[0].indices) {
        var containsGalaxy = false
        for (y in map.indices) {
            if (map[y][x] == '#') {
                galaxyCoordinates.add(Coordinate(x, y))
                containsGalaxy = true
            }
        }
        if (!containsGalaxy) {
            emptyCols.add(x)
        }
    }

    return Universe(emptyRows, emptyCols, galaxyCoordinates)
}

fun distances(expansionFactor: Int, universe: Universe): List<Long> {
    val distances = mutableListOf<Long>()
    for (i in universe.galaxies.indices) {
        for (j in i + 1..<universe.galaxies.size) {
            distances += expansedDistance(
                universe.galaxies[i],
                universe.galaxies[j],
                universe.emptyRows,
                universe.emptyCols,
                expansionFactor
            )
        }
    }
    return distances
}

fun expansedDistance(
    from: Coordinate,
    to: Coordinate,
    emptyRows: Set<Int>,
    emptyCols: Set<Int>,
    expansionFactor: Int
): Long {
    val minX = min(from.x, to.x)
    val minY = min(from.y, to.y)
    val maxX = max(from.x, to.x)
    val maxY = max(from.y, to.y)
    val emptyRowsBetween = emptyRows.count { it in minY..maxY }
    val emptyColsBetween = emptyCols.count { it in minX..maxX }

    val directDistance = manhattanDistance(from, to)

    val rowsToAdd = if (emptyRowsBetween > 0) (emptyRowsBetween * (expansionFactor - 1)) else 0
    val colsToAdd = if (emptyColsBetween > 0) (emptyColsBetween * (expansionFactor - 1)) else 0

    return directDistance.toLong() + rowsToAdd + colsToAdd
}

fun manhattanDistance(from: Coordinate, to: Coordinate) =
    abs(from.x - to.x) + abs(from.y - to.y)
