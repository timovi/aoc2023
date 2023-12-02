data class Round(val red: Int, val green: Int, val blue: Int) {
    fun possible() = red <= 12 && green <= 13 && blue <= 14
}

data class Game(val id: Int, val rounds: List<Round>) {
    fun minOfUsedCubes(): Triple<Int, Int, Int> =
        Triple(
            rounds.maxOf(Round::red),
            rounds.maxOf(Round::green),
            rounds.maxOf(Round::blue)
        )
}

fun main() {
    val input = {}::class.java.getResource("input.txt").readText()
    val games = input.split("\r\n").mapIndexed { index, row ->
        parseGame(index + 1, row)
    }

    val possibleGames = games.filter { it.rounds.all(Round::possible) }
    println("Puzzle1: ${possibleGames.sumOf(Game::id)}")

    val powerOfCubes = games.map(Game::minOfUsedCubes).sumOf { it.first * it.second * it.third }
    println("Puzzle1: $powerOfCubes")
}

fun parseGame(id: Int, gameRow: String): Game {
    val rounds = gameRow.split(": ").last().split("; ").map { parseRound(it) }
    return Game(id, rounds)
}

fun parseRound(roundRow: String): Round {
    val round = roundRow.split(", ")
    return Round(
        parseColor("red", round),
        parseColor("green", round),
        parseColor("blue", round)
    )
}

fun parseColor(color: String, round: List<String>): Int {
    val colorCount = round.find { it.contains(color) }
    return if (colorCount != null) {
        colorCount.split(" ").first().toInt()
    } else {
        0
    }
}