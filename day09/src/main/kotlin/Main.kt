import kotlin.system.measureTimeMillis

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        val newRows = rows.map { calculateRow(it) }
        println("Puzzle 1: ${newRows.sumOf { it.last() }}")
        println("Puzzle 2: ${newRows.sumOf { it.first() }}")
    }

    println("Duration: $duration ms")
}

fun calculateRow(row: String): List<Int> {
    val values = row.split(" ").map { it.toInt() }
    return calculateDifferencesToStartAndEnd(values)
}

fun calculateDifferencesToStartAndEnd(values: List<Int>): List<Int> {
    val differences = values.zipWithNext().map { it.second - it.first }

    return if (differences.toSet().size > 1) {
        val newDifferences = calculateDifferencesToStartAndEnd(differences)
        listOf(values.first() - newDifferences.first()) + values + (values.last() + newDifferences.last())
    } else {
        listOf(values.first() - differences.first()) + values + (values.last() + differences.last())
    }
}