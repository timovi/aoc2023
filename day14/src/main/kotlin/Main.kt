import kotlin.system.measureTimeMillis

val cache = mutableMapOf<String, String>()

var map = mutableListOf<MutableList<Char>>()

enum class Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST
}

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input2.txt").readText()
        val rows = input.split("\r\n")

        // Puzzle 1
        initializeMap(input)
        tilt(Direction.NORTH)
        println("Puzzle 1: ${calculateLoad()}")

        // Puzzle 2
        initializeMap(input)
        repeat(100000) { // 1000000000
            cycle()
        }
        map.forEach { row -> println(row.joinToString("")) }
        println("Puzzle 2: ${calculateLoad()}")
    }

    println("Duration: $duration ms")
}

fun initializeMap(input: String) {
    map.clear()
    input.split("\r\n").forEach { row -> map.add(row.toMutableList()) }
}

fun cycle() {
    tilt(Direction.NORTH)
    tilt(Direction.WEST)
    tilt(Direction.SOUTH)
    tilt(Direction.EAST)
}

fun tilt(direction: Direction) {
    when(direction) {
        Direction.NORTH -> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == 'O') {
                        val valuesBefore = (0..<y).map { i -> map[i][x] }.joinToString("")
                        val newPosition = newPositionBefore(valuesBefore)
                        map[y][x] = '.'
                        map[newPosition][x] = 'O'
                    }
                }
            }
        }

        Direction.WEST -> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == 'O') {
                        val valuesBefore = (0..<x).map { i -> map[y][i] }.joinToString("")
                        val newPosition = newPositionBefore(valuesBefore)
                        map[y][x] = '.'
                        map[y][newPosition] = 'O'
                    }
                }
            }
        }

        Direction.SOUTH -> {
            val maxY = map.size - 1
            for (y in map.indices.reversed()) {
                for (x in map[y].indices) {
                    if (map[y][x] == 'O') {
                        val valuesAfter = (y+1..maxY).map { i -> map[i][x] }.joinToString("")
                        val newPosition = newPositionAfter(valuesAfter, y)
                        map[y][x] = '.'
                        map[newPosition][x] = 'O'
                    }
                }
            }
        }

        Direction.EAST -> {
            val maxX = map[0].size - 1
            for (y in map.indices) {
                for (x in map[y].indices.reversed()) {
                    if (map[y][x] == 'O') {
                        val valuesAfter = (x+1..maxX).map { i -> map[y][i] }.joinToString("")
                        val newPosition = newPositionAfter(valuesAfter, x)
                        map[y][x] = '.'
                        map[y][newPosition] = 'O'
                    }
                }
            }
        }
    }
}

fun newPositionBefore(row: String): Int {
    return when (val stop = row.lastIndexOfAny(charArrayOf('O', '#'))) {
        -1 -> 0
        else -> stop + 1
    }
}

fun newPositionAfter(row: String, startIndex: Int): Int {
    return when (val stop = row.indexOfAny(charArrayOf('O', '#'))) {
        -1 -> startIndex + row.length
        else -> startIndex + stop
    }
}

fun calculateLoad(): Long {
    val maxWeight = map.size.toLong()
    var totalWeight = 0L
    for (y in map.indices) {
        for (x in map[y].indices) {
            if (map[y][x] == 'O') {
                totalWeight += maxWeight - y
            }
        }
    }
    return totalWeight
}