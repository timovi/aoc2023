data class PartNumber(val number: Int, val paddedRows: IntRange, val paddedColumns: IntRange)

fun main() {
    val input = {}::class.java.getResource("input.txt").readText()
    val partNumbers = partNumbers(input)

    // Puzzle 1
    val symbolCoordinates = symbolCoordinates("[^0-9]+", input)
    val adjacentPartNumbers =
        partNumbers.filter { partNumber ->
            symbolCoordinates.any { symbolCoordinate ->
                val (x, y) = symbolCoordinate
                partNumber.paddedRows.contains(y) &&
                    partNumber.paddedColumns.contains(x)
            }
        }
    println("Puzzle 1: ${adjacentPartNumbers.sumOf(PartNumber::number)}")

    // Puzzle 2
    val gearsCoordinates = symbolCoordinates("\\*", input)
    val gearRatios =
        gearsCoordinates
            .map { gearCoordinate ->
                val (x, y) = gearCoordinate
                val gearPartNumbers = partNumbers.filter { partNumber ->
                    partNumber.paddedRows.contains(y) &&
                        partNumber.paddedColumns.contains(x)
                }

                when (gearPartNumbers.size) {
                    2 -> gearPartNumbers.first().number * gearPartNumbers.last().number
                    else -> 0
                }
            }
    println("Puzzle 2: ${gearRatios.sum()}")
}

fun symbolCoordinates(pattern: String, input: String): List<Pair<Int, Int>> =
    input
        .split("\r\n")
        .mapIndexed { rowIndex, row ->
            val rowWithoutDots = row.replace(".", "0")
            Regex(pattern).findAll(rowWithoutDots)
                .map { Pair(it.range.first, rowIndex) }
                .toList()
        }
        .flatten()

fun partNumbers(input: String): List<PartNumber> =
    input
        .split("\r\n")
        .mapIndexed { rowIndex, row ->
            partNumbersFromRow(rowIndex, row)
        }
        .flatten()


fun partNumbersFromRow(rowIndex: Int, row: String): List<PartNumber> =
    Regex("[0-9]+").findAll(row)
        .map { result ->
            val intValue = result.value.toInt()
            val rowIndices = IntRange(rowIndex - 1, rowIndex + 1)
            val columnIndices = IntRange(result.range.first - 1, result.range.last + 1)
            PartNumber(intValue, rowIndices, columnIndices)
        }
        .toList()
