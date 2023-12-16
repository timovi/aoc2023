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
        println("Puzzle 2: ")
    }

    println("Duration: $duration ms")
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
    when (map[y][x]) {
        '.' -> {
            when (direction) {
                Direction.UP -> setOf(Triple(x, y - 1, Direction.UP))
                Direction.RIGHT -> setOf(Triple(x + 1, y, Direction.RIGHT))
                Direction.DOWN -> setOf(Triple(x, y + 1, Direction.DOWN))
                Direction.LEFT -> setOf(Triple(x - 1, y, Direction.LEFT))
            }
        }

        '|' -> {
            when (direction) {
                Direction.UP -> setOf(Triple(x, y - 1, Direction.UP))
                Direction.DOWN -> setOf(Triple(x, y + 1, Direction.DOWN))
                Direction.LEFT, Direction.RIGHT -> setOf(
                    Triple(x, y - 1, Direction.UP),
                    Triple(x, y + 1, Direction.DOWN)
                )
            }
        }

        '-' -> {
            when (direction) {
                Direction.UP, Direction.DOWN -> setOf(
                    Triple(x - 1, y, Direction.LEFT),
                    Triple(x + 1, y, Direction.RIGHT)
                )

                Direction.LEFT -> setOf(Triple(x - 1, y, Direction.LEFT))
                Direction.RIGHT -> setOf(
                    Triple(x + 1, y, Direction.RIGHT)
                )
            }
        }

        '/' -> {
            when (direction) {
                Direction.UP -> setOf(Triple(x + 1, y, Direction.RIGHT))
                Direction.RIGHT -> setOf(Triple(x, y - 1, Direction.UP))
                Direction.DOWN -> setOf(Triple(x - 1, y, Direction.LEFT))
                Direction.LEFT -> setOf(Triple(x, y + 1, Direction.DOWN))
            }
        }

        '\\' -> {
            when (direction) {
                Direction.UP -> setOf(Triple(x - 1, y, Direction.LEFT))
                Direction.RIGHT -> setOf(Triple(x, y + 1, Direction.DOWN))
                Direction.DOWN -> setOf(Triple(x + 1, y, Direction.RIGHT))
                Direction.LEFT -> setOf(Triple(x, y - 1, Direction.UP))
            }
        }

        else -> throw Exception("Unknown character")
    }