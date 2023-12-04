import kotlin.math.pow

data class Game(val id: Int, val winCount: Int, val score: Int)

fun main() {
    val input = {}::class.java.getResource("input.txt").readText()
    val rows = input.split("\r\n")

    val games = rows.mapIndexed { index, row -> parseGame(index + 1, row) }
    println("Puzzle 1: ${games.sumOf(Game::score)}")

    val gameWinMap = games.associate { it.id to 1 }.toMutableMap()
    games.forEach { game ->
        val currentGameCardCount = gameWinMap[game.id]!!
        (game.id + 1 ..game.id + game.winCount).forEach { id ->
            val wonGameCards = gameWinMap[id]!!
            gameWinMap[id] = currentGameCardCount + wonGameCards
        }
    }
    println("Puzzle 2: ${gameWinMap.values.sum()}")
}

fun parseGame(index: Int, gameRow: String): Game {
    val row = gameRow
        .split(": ")
        .last()
        .split(" | ")

    val winningNumbers = row.first().split(" ").filter { it.isNotBlank() }.map { it.toInt() }.toSet()
    val ticketNumbers = row.last().split(" ").filter { it.isNotBlank() }.map { it.toInt() }.toSet()

    val winCount = winningNumbers.intersect(ticketNumbers).count()
    val score = when(winCount) {
        0 -> 0
        else -> 2.0.pow(winCount - 1).toInt()
    }

    return Game(index, winCount, score)
}
