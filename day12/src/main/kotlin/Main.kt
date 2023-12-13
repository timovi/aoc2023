import kotlin.system.measureTimeMillis

data class Record(val springs: String, val groups: List<Int>)
data class CacheKey(val index: Int, val groupLoopIndex: Int, val groupSize: Int)
val cache = mutableMapOf<CacheKey, Long>()

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val records = input.split("\r\n").map { parseRecord(it) }

        // Puzzle 1
        val records1 = records.map(::expandRecord1)
        val result1 = records1.sumOf {
            cache.clear()
            sumOfPositions(it, 0, 0, 0)
        }
        println("Puzzle 1: $result1")

        // Puzzle 2
        val records2 = records.map(::expandRecord2)
        val result2 = records2.sumOf {
            cache.clear()
            sumOfPositions(it, 0, 0, 0)
        }
        println("Puzzle 2: $result2")
    }

    println("Duration: $duration ms")
}

fun parseRecord(row: String): Record {
    val (springs, damagedGroups) = row.split(" ")
    return Record(springs, damagedGroups.split(",").map { it.toInt() })
}

fun expandRecord1(record: Record): Record {
    return Record("${record.springs}.", record.groups)
}

fun expandRecord2(record: Record): Record {
    val springs = (1..5).joinToString("?") { record.springs }
    val groups = (1..5).flatMap { record.groups }
    return Record("${springs}.", groups)
}

fun sumOfPositions(record: Record, index: Int, groupIndex: Int, groupLoopIndex: Int): Long {
    val cacheKey = CacheKey(index, groupLoopIndex, groupIndex)

    if (cache.containsKey(cacheKey)) {
        return cache[cacheKey]!!
    }

    if (index == record.springs.length) {
        return if (groupIndex == record.groups.size && groupLoopIndex == 0) 1 else 0
    }

    var sum = 0L

    if (record.springs[index] in ".?") {
        if (groupIndex < record.groups.size && groupLoopIndex == record.groups[groupIndex]) {
            sum += sumOfPositions(record, index + 1, groupIndex + 1, 0)
        }
        if (groupLoopIndex == 0) {
            sum += sumOfPositions(record, index + 1, groupIndex, groupLoopIndex)
        }
    }

    if (record.springs[index] in "#?") {
        sum += sumOfPositions(record, index + 1, groupIndex, groupLoopIndex + 1)
    }

    cache[cacheKey] = sum

    return sum
}