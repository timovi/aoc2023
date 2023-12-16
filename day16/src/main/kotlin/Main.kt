import kotlin.system.measureTimeMillis

var map = emptyList<String>()

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        map = input.split("\r\n")

        // Puzzle 1
        val beams = mutableSetOf<Triple<Int, Int, Direction>>()
        val energized = mutableSetOf<Pair<Int, Int>>()
        fireBeam(0, 0, Direction.RIGHT, beams, energized)
        println("Puzzle 1: ${energized.size}")

        // Puzzle 2
        val maxY = map.indices.last
        val maxX = map[0].indices.last
        val startCoordinates =
            map[0].indices.map { x -> Triple(x, 0, Direction.DOWN) } +
            map.indices.map { y -> Triple(maxX, y, Direction.LEFT) } +
            map[0].indices.map { x -> Triple(x, maxY, Direction.UP) } +
            map.indices.map { y -> Triple(0, y, Direction.RIGHT) }
        println("Puzzle 2: ${maxEnergizedFromCoordinates(startCoordinates)}")
    }

    println("Duration: $duration ms")
}

fun maxEnergizedFromCoordinates(coordinates: List<Triple<Int, Int, Direction>>) =
    coordinates.maxOf { coordinate ->
        val (x, y, direction) = coordinate
        val beams = mutableSetOf<Triple<Int, Int, Direction>>()
        val energized = mutableSetOf<Pair<Int, Int>>()
        fireBeam(x, y, direction, beams, energized)
        energized.size
    }

fun fireBeam(
    x: Int, y: Int, direction: Direction,
    beams: MutableSet<Triple<Int, Int, Direction>>,
    energized: MutableSet<Pair<Int, Int>>
) {
    if (beams.contains(Triple(x, y, direction))) {
        return
    } else {
        beams.add(Triple(x, y, direction))
        energized.add(x to y)
    }

    val nextLocations = nextLocations(x, y, direction)
    nextLocations
        .filter { isInsideMap(it.first, it.second) }
        .forEach {
            val (newX, newY, newDirection) = it
            fireBeam(newX, newY, newDirection, beams, energized)
        }
}

fun isInsideMap(x: Int, y: Int) = x >= 0 && x < map[0].length && y >= 0 && y < map.size

fun nextLocations(x: Int = 0, y: Int = 0, direction: Direction): Set<Triple<Int, Int, Direction>> =
    when (map[y][x] to direction) {
        '.' to Direction.UP -> setOf(Triple(x, y - 1, Direction.UP))
        '.' to Direction.RIGHT -> setOf(Triple(x + 1, y, Direction.RIGHT))
        '.' to Direction.DOWN -> setOf(Triple(x, y + 1, Direction.DOWN))
        '.' to Direction.LEFT -> setOf(Triple(x - 1, y, Direction.LEFT))

        '|' to Direction.UP -> setOf(Triple(x, y - 1, Direction.UP))
        '|' to Direction.DOWN -> setOf(Triple(x, y + 1, Direction.DOWN))
        '|' to Direction.LEFT,
        '|' to Direction.RIGHT -> setOf(
            Triple(x, y - 1, Direction.UP),
            Triple(x, y + 1, Direction.DOWN))

        '-' to Direction.UP,
        '-' to Direction.DOWN -> setOf(
            Triple(x - 1, y, Direction.LEFT),
            Triple(x + 1, y, Direction.RIGHT))
        '-' to Direction.LEFT -> setOf(Triple(x - 1, y, Direction.LEFT))
        '-' to Direction.RIGHT -> setOf(Triple(x + 1, y, Direction.RIGHT))

        '/' to Direction.UP -> setOf(Triple(x + 1, y, Direction.RIGHT))
        '/' to Direction.RIGHT -> setOf(Triple(x, y - 1, Direction.UP))
        '/' to Direction.DOWN -> setOf(Triple(x - 1, y, Direction.LEFT))
        '/' to Direction.LEFT -> setOf(Triple(x, y + 1, Direction.DOWN))

        '\\' to Direction.UP -> setOf(Triple(x - 1, y, Direction.LEFT))
        '\\' to Direction.RIGHT -> setOf(Triple(x, y + 1, Direction.DOWN))
        '\\' to Direction.DOWN -> setOf(Triple(x + 1, y, Direction.RIGHT))
        '\\' to Direction.LEFT -> setOf(Triple(x, y - 1, Direction.UP))
        
        else -> throw Exception("Unknown character")
    }