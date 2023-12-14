import kotlin.system.measureTimeMillis

data class Map(val rows: List<String>, val columns: List<String>)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val maps = parseMaps(input)

        // Puzzle 1
        val result1 =
            (maps.sumOf { findMirroredIndex(it.columns) + 1 }) * 100 +
                maps.sumOf { findMirroredIndex(it.rows) + 1 }
        println("Puzzle 1: $result1")

        // Puzzle 2
        val result2 =
            (maps.sumOf { findMirroredIndexWithSmudge(it.columns) + 1 }) * 100 +
                maps.sumOf { findMirroredIndexWithSmudge(it.rows) + 1 }
        println("Puzzle 2: $result2")
    }

    println("Duration: $duration ms")
}

fun parseMaps(input: String): List<Map> {
    val maps = mutableListOf<Map>()
    val rows = input.split("\r\n")

    val mapRows = mutableListOf<String>()
    for (rowIndex in rows.indices) {
        val row = rows[rowIndex]
        if (row.isEmpty()) {
            maps.add(Map(mapRows.toList(), transpose(mapRows)))
            mapRows.clear()
        } else if (rowIndex == rows.size - 1) {
            mapRows.add(row)
            maps.add(Map(mapRows.toList(), transpose(mapRows)))
        } else {
            mapRows.add(row)
        }
    }

    return maps
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

fun findMirroredIndex(rows: List<String>) =
    rows.drop(1)
        .fold(findMirrorIndicesOfRow(rows.first())) { commonIndices, row ->
            commonIndices.intersect(findMirrorIndicesOfRow(row))
        }.firstOrNull() ?: -1

fun findMirroredIndexWithSmudge(rows: List<String>): Int {
    val rowIndices = rows.flatMap(::findMirrorIndicesOfRow).groupingBy { it }.eachCount()
    val almostMatchingCount = rows.size - 1
    return rowIndices.entries.filter { it.value == almostMatchingCount }.map { it.key }.firstOrNull() ?: -1
}

fun findMirrorIndicesOfRow(row: String) =
    (0..<row.indices.last).filter { index -> isMirrored(row, index) }.toSet()


fun isMirrored(row: String, index: Int, spread: Int = 0): Boolean {
    return if (index - spread < 0 || index + spread + 1 >= row.length) {
        true
    } else {
        row[index - spread] == row[index + 1 + spread] && isMirrored(row, index, spread + 1)
    }
}