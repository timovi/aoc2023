import kotlin.system.measureTimeMillis

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()

        // Puzzle 1
        println("Puzzle 1: ${calculateLoad(tilt(input))}")

        // Puzzle 2

        println("Puzzle 2: ")
    }

    println("Duration: $duration ms")
}

fun tilt(input: String): List<String> {
    val transposedMap = transpose(input.split("\r\n"))

    val tiltedMap = transposedMap.map { row ->
        row.split("#").joinToString("#") { it.toCharArray().sorted().reversed().joinToString("") }
    }

    return tiltedMap
}

fun calculateLoad(map: List<String>): Long {
    val maxWeight = map[0].length.toLong()
    return map.sumOf { row ->
        var rowWeight = 0L
        for (x in row.indices) {
            rowWeight +=
                when (row[x]) {
                    'O' -> maxWeight - x
                    else -> 0
                }
        }
        rowWeight
    }
}

fun transpose(map: List<String>): List<String> {
    val columns = map[0].length
    val rows = map.size
    return List(columns) { j ->
        List(rows) { i ->
            map[i][j]
        }.joinToString("")
    }
}