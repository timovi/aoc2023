import kotlin.system.measureTimeMillis

data class Tile(val char: Char, val y: Int, val x: Int)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val map = input.split("\r\n").map { it.toList() }
        val start = findStart(map)

        val mappedPipeSystem = mapPipeSystem(map, start)
        println("Puzzle 1: ${mappedPipeSystem.size / 2}")
        println("Puzzle 2: ${insideTileCount(mappedPipeSystem)}")
    }

    println("Duration: $duration ms")
}

fun mapPipeSystem(map: List<List<Char>>, start: Tile): List<Tile> {
    val visited = mutableListOf(Tile('S', start.y, start.x))

    var next: Tile? = start
    while (next != null) {
        val nextTiles = nextCoordinates(next).map { Tile(map[it.first][it.second], it.first, it.second)}
        next = nextTiles.firstOrNull() { !visited.contains(it) }
        if (next != null) visited.add(next)
    }

    return visited
}

fun findStart(map: List<List<Char>>): Tile {
    for (y in map.indices) {
        for (x in map[y].indices) {
            if (map[y][x] == 'S') {
                return Tile(defineStartPipeChar(y, x, map), y, x)
            }
        }
    }
    throw Exception("No start found")
}

fun insideTileCount(pipes: List<Tile>): Int {
    val minY = pipes.minBy { it.y }.y
    val maxY = pipes.maxBy { it.y }.y
    val minX = pipes.minBy { it.x }.x
    val maxX = pipes.maxBy { it.x }.x

    val pipeMap = Array(maxY - minY + 1) { Array(maxX - minX + 1) { 0 } }
    pipes.mapIndexed { index, tile -> pipeMap[tile.y - minY][tile.x - minX] = index + 1 }

    var directionCounter = 0
    var insideTileCount = 0
    for (y in pipeMap.indices) {
        for (x in pipeMap[y].indices) {
            if (y == pipeMap.size - 1) {
                return insideTileCount
            }
            val tile = pipeMap[y][x]
            val tileBelow = pipeMap[y + 1][x]
            if (tileBelow != 0) {
                when (tile - tileBelow) {
                    1 -> directionCounter++
                    -1 -> directionCounter--
                }
            } else {
                if (directionCounter != 0) {
                    insideTileCount++
                }
            }
        }
    }
    return insideTileCount
}

fun defineStartPipeChar(y: Int, x: Int, map: List<List<Char>>): Char {
    val pipeToNorth = (y > 0 && map[y - 1][x] in setOf('7', '|', 'F'))
    val pipeToEast = (x < map[y].size - 1 && map[y][x + 1] in setOf('J', '-', '7'))
    val pipeToSouth = (y < map.size - 1 && map[y + 1][x] in setOf('J', '|', 'L'))
    val pipeToWest = (x > 0 && map[y][x - 1] in setOf('L', '-', 'F'))

    return if (pipeToNorth && pipeToSouth) '|'
    else if (pipeToWest && pipeToEast) '-'
    else if (pipeToNorth && pipeToEast) 'L'
    else if (pipeToNorth && pipeToWest) 'J'
    else if (pipeToSouth && pipeToWest) '7'
    else if (pipeToSouth && pipeToEast) 'F'
    else throw Exception("No start pipe found")
}

fun nextCoordinates(tile: Tile): Set<Pair<Int, Int>> {
    return when (tile.char) {
        '|' -> setOf(Pair(tile.y - 1, tile.x), Pair(tile.y + 1, tile.x))
        '-' -> setOf(Pair(tile.y, tile.x - 1), Pair(tile.y, tile.x + 1))
        'L' -> setOf(Pair(tile.y - 1, tile.x), Pair(tile.y, tile.x + 1))
        'J' -> setOf(Pair(tile.y - 1, tile.x), Pair(tile.y, tile.x - 1))
        '7' -> setOf(Pair(tile.y + 1, tile.x), Pair(tile.y, tile.x - 1))
        'F' -> setOf(Pair(tile.y + 1, tile.x), Pair(tile.y, tile.x + 1))
        else -> emptySet()
    }
}