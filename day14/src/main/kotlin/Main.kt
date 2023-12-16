import kotlin.system.measureTimeMillis

var map = mutableListOf<MutableList<Char>>()

enum class Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST
}

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()

        // Puzzle 1
        initializeMap(input)
        tilt(Direction.NORTH)
        println("Puzzle 1: ${calculateLoad()}")

        // Puzzle 2
        initializeMap(input)
        println("Puzzle 2: ${runCycles(1000000000)}")
    }

    println("Duration: $duration ms")
}

fun initializeMap(input: String) {
    map.clear()
    input.split("\r\n").forEach { row -> map.add(row.toMutableList()) }
}

fun runCycles(maxCycles: Int): Long {
    val state = mutableMapOf(getState() to 0)

    repeat(1000) { index ->
        cycle()

        val newState = getState()
        if (state.contains(newState)) {
            val loopStart = state[newState]!!
            val loopSize = index - loopStart
            val remaining = (maxCycles - index - 1) % loopSize
            repeat(remaining) {
                cycle()
            }
            return calculateLoad()
        }
        state[newState] = index
    }

    return 0
}

fun getState() = map.joinToString("|") { row -> row.joinToString("") }

fun cycle() {
    tilt(Direction.NORTH)
    tilt(Direction.WEST)
    tilt(Direction.SOUTH)
    tilt(Direction.EAST)
}

fun tilt(direction: Direction) {
    when(direction) {
        Direction.NORTH -> {
            val maxY = map.size - 1
            for (x in map[0].indices) {
                val column = (0..maxY).map { y -> map[y][x] }.joinToString("")
                val newColumn = tiltColumnToNorth(column)
                (0..maxY).forEach { y -> map[y][x] = newColumn[y] }
            }
        }

        Direction.WEST -> {
            for (y in map.indices) {
                val row = map[y].joinToString("")
                val newRow = tiltRowToWest(row)
                map[y] = newRow.toMutableList()
            }
        }

        Direction.SOUTH -> {
            val maxY = map.size - 1
            for (x in map[0].indices) {
                val column = (0..maxY).map { y -> map[y][x] }.joinToString("")
                val newColumn = tiltColumnToSouth(column)
                (0..maxY).forEach { y -> map[y][x] = newColumn[y] }
            }
        }

        Direction.EAST -> {
            for (y in map.indices) {
                val row = map[y].joinToString("")
                val newRow = tiltRowToEast(row)
                map[y] = newRow.toMutableList()
            }
        }
    }
}


fun tiltColumnToNorth(column: String): String {
    return column.split("#").joinToString("#") { part -> part.toList().sorted().reversed().joinToString("") }
}

fun tiltRowToWest(row: String): String {
    return row.split("#").joinToString("#") { part -> part.toList().sorted().reversed().joinToString("") }
}

fun tiltColumnToSouth(column: String): String {
    return column.split("#").joinToString("#") { part -> part.toList().sorted().joinToString("") }
}

fun tiltRowToEast(row: String): String {
    return row.split("#").joinToString("#") { part -> part.toList().sorted().joinToString("") }
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