import kotlin.system.measureTimeMillis

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        val races = parseRaces(rows)
        val winCounts = races.map { possibleWinsCount(it) }.fold(1) { prev, curr -> prev * curr }
        println("Puzzle 1: $winCounts")

        val race = parseRace(rows)
        val winCount = possibleWinsCount(race)
        println("Puzzle 2: $winCount")
    }

    println("Duration: $duration ms")
}

fun parseRaces(rows: List<String>): List<Pair<Long, Long>> {
    val parseLongs = { row: String -> row.split(" ").drop(1).filter { it.isNotBlank() }.map { it.toLong() } }
    return parseLongs(rows.first()).zip(parseLongs(rows.last()))
}

fun parseRace(rows: List<String>): Pair<Long, Long> {
    val parseLong = { row: String -> row.replace(" ", "").split(":").last().toLong() }
    return Pair(parseLong(rows.first()), parseLong(rows.last()))
}

fun possibleWinsCount(race: Pair<Long, Long>) = distances(race.first).count { it > race.second }
fun distances(duration: Long) = (1..<duration).map { distance(it, duration - it) }
fun distance(speed: Long, duration: Long) = speed * duration
