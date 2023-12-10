import kotlin.system.measureTimeMillis

data class Tile(val char: Char, val y: Int, val x: Int)
data class Loop(val tiles: List<Tile>)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val map = input.split("\r\n").map { it.toList() }
        val start = findStart(map)

        // Puzzle 1
        val mappedPipeSystem = mapPipeSystem(map, start)
        println("Puzzle 1: ${mappedPipeSystem.size / 2}")

        // Puzzle 2
        //val loops = mapLoops(mappedPipeSystem)
        //println(loops)
        println("Puzzle 2: ")
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

fun mapLoops(pipes: Set<Tile>): Int {
    val minY = pipes.minBy { it.y }.y
    val maxY = pipes.maxBy { it.y }.y
    val minX = pipes.minBy { it.x }.x
    val maxX = pipes.maxBy { it.x }.x

    val pipeMap = Array(maxY - minY + 1) { Array(maxX - minX + 1) { 0 } }
    //pipes.forEach() { pipeMap[it.y][it.x] = it.stepNumber + 1 }

    println(minY)
    println(maxY)
    println(minX)
    println(maxX)
    println(pipes)

    pipeMap.forEach { println(it.joinToString(" ")) }


/*    for (y in minY..maxY) {
        val row = pipes.filter { it.y == y }
        val minX = row.minBy { it.x }.x
        val maxX = row.maxBy { it.x }.x

        for (x in minX..maxX) {
            if (map[y][x] == 'S') {
                return findPipeSystemLoops(map, Tile(defineStartPipeChar(y, x, map), y, x)).first().tiles.size
            }
        }
    }
*/
    return 0
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