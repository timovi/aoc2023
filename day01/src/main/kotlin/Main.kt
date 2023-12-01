fun main() {
    val input = {}::class.java.getResource("input.txt").readText()
    val rows = input.split("\r\n")

    val puzzle1Digits = rows.map {
        val firstAndLastDigit = firstAndLastDigit(it, emptyList())
        toNumber(firstAndLastDigit.first, firstAndLastDigit.second)
    }
    println("Puzzle 1: ${puzzle1Digits.sumOf { it }}")

    val puzzle2Digits = rows.map {
        val firstAndLastDigit = firstAndLastDigit(it, digitsAsString)
        toNumber(firstAndLastDigit.first, firstAndLastDigit.second)
    }
    println("Puzzle 2: ${puzzle2Digits.sumOf { it }}")
}

fun toNumber(firstDigit: Int, lastDigit: Int) = "$firstDigit$lastDigit".toInt()

fun firstAndLastDigit(row: String, stringDigits: List<String>): Pair<Int, Int> {
    val stringDigitsWithIndex =
        stringDigits
            .mapIndexed { index, digitAsString ->
                listOf(
                    Pair(index + 1, row.indexOf(digitAsString)),
                    Pair(index + 1, row.lastIndexOf(digitAsString)))
            }.flatten()
    val intDigitsWithIndex = row.mapIndexed { index, char ->
        if (char.isDigit()) {
            Pair(char.digitToInt(), index)
        } else {
            Pair(0, -1)
        }
    }

    val digits = (stringDigitsWithIndex + intDigitsWithIndex).filter { it.second != -1 }
    return Pair(digits.minBy { it.second }.first, digits.maxBy { it.second }.first)
}

val digitsAsString = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")