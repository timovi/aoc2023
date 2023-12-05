import kotlin.system.measureTimeMillis

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        val seed2soil = parseSourceToDestination("seed-to-soil", rows)
        val soil2fertilizer = parseSourceToDestination("soil-to-fertilizer", rows)
        val fertilizer2water = parseSourceToDestination("fertilizer-to-water", rows)
        val water2light = parseSourceToDestination("water-to-light", rows)
        val light2temperature = parseSourceToDestination("light-to-temperature", rows)
        val temperature2humidity = parseSourceToDestination("temperature-to-humidity", rows)
        val humidity2location = parseSourceToDestination("humidity-to-location", rows)

        val seeds1 = parseSeeds(rows.first())
        val locations1 = seeds1.map { seed ->
            val soil = getDestination(seed, seed2soil)
            val fertilizer = getDestination(soil, soil2fertilizer)
            val water = getDestination(fertilizer, fertilizer2water)
            val light = getDestination(water, water2light)
            val temperature = getDestination(light, light2temperature)
            val humidity = getDestination(temperature, temperature2humidity)
            getDestination(humidity, humidity2location)
        }

        println("Puzzle 1: ${locations1.min()}")

        val seeds2 = parseSeedsRanges(rows.first())
        val locations2 = seeds2.map { seedsRange ->
            var minLocation = Long.MAX_VALUE
            var selectedSeed = 0L
            for (seed in seedsRange) {
                val soil = getDestination(seed, seed2soil)
                val fertilizer = getDestination(soil, soil2fertilizer)
                val water = getDestination(fertilizer, fertilizer2water)
                val light = getDestination(water, water2light)
                val temperature = getDestination(light, light2temperature)
                val humidity = getDestination(temperature, temperature2humidity)
                val location = getDestination(humidity, humidity2location)

                if (location < minLocation) {
                    minLocation = location
                    selectedSeed = seed
                }
            }
            Triple(seedsRange, selectedSeed, minLocation)

        }
        println(locations2.sortedBy { it.third })
        println("Puzzle 2: ${locations2.minOf { it.third }}")
    }

    println("Duration: $duration ms")
}

fun parseSeeds(row: String) =
    row.split(": ").last().split(" ").map { it.toLong() }

fun parseSeedsRanges(row: String) =
    row.split(": ").last().split(" ").windowed(2, 2).map {
        val (initialSeed, length) = it
        initialSeed.toLong()..<initialSeed.toLong() + length.toLong()
    }

fun parseSourceToDestination(category: String, rows: List<String>): List<Pair<LongRange, LongRange>> =
    rows
        .dropWhile { !it.startsWith(category) }
        .drop(1)
        .takeWhile { it.isNotBlank() }
        .map { row ->
            val (destination, source, length) = row.split(" ")
            Pair(
                source.toLong()..source.toLong() + length.toLong(),
                destination.toLong()..destination.toLong() + length.toLong()
            )
        }

fun getDestination(source: Long, destination: List<Pair<LongRange, LongRange>>): Long {
    val mapping = destination.firstOrNull() { it.first.contains(source) }
    return if (mapping != null) {
        val offset = mapping.second.first - mapping.first.first
        source + offset
    } else {
        source
    }
}