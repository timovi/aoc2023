import kotlin.system.measureTimeMillis

data class Record(val springs: String, val damagedGroups: List<Int>)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val records = input.split("\r\n").map { parseRecord(it) }

        // Puzzle1
        val records1 = records.map(::expandRecord1)
        println("Puzzle 1: ${records1.sumOf(::findPositionCountsOfDamagedGroups)}")

        println("Puzzle 2: ")
    }

    println("Duration: $duration ms")
}

fun parseRecord(row: String): Record {
    val (springs, damagedGroups) = row.split(" ")
    return Record(springs, damagedGroups.split(",").map { it.toInt() })
}

fun expandRecord1(record: Record): Record {
    return Record(".${record.springs}.", record.damagedGroups)
}

fun findPositionCountsOfDamagedGroups(record: Record): Long {
    var positions = listOf(record.springs)
    for (groupIndex in record.damagedGroups.indices) {
        val group = record.damagedGroups[groupIndex]
        val restGroups = record.damagedGroups.drop(groupIndex + 1)
        val restSpringsMinLength = restGroups.sum() + restGroups.size - 1

        val newPositions = mutableListOf<String>()
        for (position in positions) {
            newPositions += findPositionsOfDamagedGroup(position, group, restSpringsMinLength)
        }
        positions = newPositions
    }
    return positions.filter { !it.contains('#') }.size.toLong()
}

fun findPositionsOfDamagedGroup(springs: String, groupSize: Int, restSpringsMinLength: Int): List<String> {
    val positions = mutableListOf<String>()

    val startIndex = springs.lastIndexOf('S') + 2
    for (index in startIndex..<springs.length) {
        if (springs.length - index < restSpringsMinLength) {
            return positions
        }

        val part = springs.drop(index).take(groupSize)

        val group = "#".repeat(groupSize)

        if (groupFits(group, part, index, groupSize, springs)) {
            positions.add(reservePositions(springs, index, groupSize))
            return positions
        }

        val possible = part.replace('?', '#')

        if (groupFits(group, possible, index, groupSize, springs)) {
            positions.add(reservePositions(springs, index, groupSize))
        }

        if (part.startsWith('#')) {
            return positions
        }
    }
    return positions
}

fun groupFits(group: String, part: String, index: Int, groupSize: Int, springs: String) =
    group == part && springs[index - 1] != '#' && springs[index + groupSize] != '#'

fun reservePositions(springs: String, start: Int, length: Int): String {
    return springs.replaceRange(start, start + length, "S".repeat(length))
}