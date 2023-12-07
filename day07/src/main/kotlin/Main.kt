import kotlin.system.measureTimeMillis

data class Hand(val cardValues: List<Int>, val rank: Int, val bid: Int)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        // Puzzle 1
        val winningsWithoutJoker = rows
            .map(::parseHandWithoutJoker)
            .sortedWith(::compareHands)
            .mapIndexed { index, hand -> hand.bid * (index + 1) }
        println("Puzzle 1: ${winningsWithoutJoker.sum()}")

        // Puzzle 2
        val winningsWithJoker = rows
            .map(::parseHandWithJoker)
            .sortedWith(::compareHands)
            .mapIndexed { index, hand -> hand.bid * (index + 1) }
        println("Puzzle 2: ${winningsWithJoker.sum()}")
    }

    println("Duration: $duration ms")
}

fun parseHandWithoutJoker(row: String): Hand {
    val (hand, bid) = row.split(" ")
    val cardValues = hand.map(::cardValueWithoutJoker)
    return Hand(cardValues, rankWithoutJoker(cardValues), bid.toInt())
}

fun parseHandWithJoker(row: String): Hand {
    val (hand, bid) = row.split(" ")
    val cardValues = hand.map(::cardValueWithJoker)
    return Hand(cardValues, rankWithJoker(cardValues), bid.toInt())
}

fun cardValueWithoutJoker(card: Char) = when (card) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> 11
    'T' -> 10
    else -> card.digitToInt()
}

fun cardValueWithJoker(card: Char) = when (card) {
    'J' -> 1
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'T' -> 10
    else -> card.digitToInt()
}

fun rankWithoutJoker(cardValues: List<Int>): Int {
    val cardCounts = cardValues.sortedDescending().groupingBy { it }.eachCount()

    val cardPairs = cardCounts.filter { it.value == 2 }.keys
    val cardTriples = cardCounts.filter { it.value == 3 }.keys
    val cardQuads = cardCounts.filter { it.value == 4 }.keys

    val isFiveOfAKind = cardCounts.size == 1
    val isFourOfAKind = cardQuads.isNotEmpty()
    val isFullHouse = cardTriples.isNotEmpty() && cardPairs.isNotEmpty()
    val isThreeOfAKind = cardTriples.isNotEmpty()
    val isTwoPairs = cardPairs.size == 2
    val isOnePair = cardPairs.size == 1

    return when {
        isFiveOfAKind -> 6
        isFourOfAKind -> 5
        isFullHouse -> 4
        isThreeOfAKind -> 3
        isTwoPairs -> 2
        isOnePair -> 1
        else -> 0
    }
}

fun rankWithJoker(cardValues: List<Int>): Int {
    val cardCounts = cardValues.filter { it != 1 }.sortedDescending().groupingBy { it }.eachCount()

    val cardPairs = cardCounts.filter { it.value == 2 }.keys
    val cardTriples = cardCounts.filter { it.value == 3 }.keys
    val cardQuads = cardCounts.filter { it.value == 4 }.keys
    val cardQuintets = cardCounts.filter { it.value == 5 }.keys

    val jokers = cardValues.filter { it == 1 }

    val isFiveOfAKind = cardQuintets.isNotEmpty()
        || (cardQuads.isNotEmpty() && jokers.size == 1)
        || (cardTriples.isNotEmpty() && jokers.size == 2)
        || (cardPairs.isNotEmpty() && jokers.size == 3)
        || jokers.size >= 4
    val isFourOfAKind = cardQuads.isNotEmpty()
        || (cardTriples.isNotEmpty() && jokers.size == 1)
        || (cardPairs.isNotEmpty() && jokers.size == 2)
        || jokers.size == 3
    val isFullHouse = (cardTriples.isNotEmpty() && cardPairs.isNotEmpty())
        || (cardPairs.size == 2 && jokers.size == 1)
    val isThreeOfAKind = cardTriples.isNotEmpty()
        || (cardPairs.isNotEmpty() && jokers.size == 1)
        || jokers.size == 2
    val isTwoPairs = cardPairs.size == 2
    val isOnePair = cardPairs.size == 1 ||
        (cardPairs.isEmpty() && jokers.size == 1)

    return when {
        isFiveOfAKind -> 6
        isFourOfAKind -> 5
        isFullHouse -> 4
        isThreeOfAKind -> 3
        isTwoPairs -> 2
        isOnePair -> 1
        else -> 0
    }
}

fun compareHands(hand1: Hand, hand2: Hand): Int {
    val rankComparison = hand1.rank.compareTo(hand2.rank)
    if (rankComparison != 0) return rankComparison

    for (i in 0..4) {
        val cardComparison = hand1.cardValues[i].compareTo(hand2.cardValues[i])
        if (cardComparison != 0) return cardComparison
    }
    return 0
}