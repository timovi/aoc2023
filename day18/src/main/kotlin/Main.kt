import kotlin.math.abs
import kotlin.system.measureTimeMillis

data class Coordinate(val x: Int, val y: Int)

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        fun parse(char: Char): Direction {
            return when (char) {
                'U' -> UP
                'R' -> RIGHT
                'D' -> DOWN
                'L' -> LEFT
                else -> throw IllegalArgumentException("Invalid direction: $char")
            }
        }

        fun parse(number: Int): Direction {
            return when (number) {
                0 -> RIGHT
                1 -> DOWN
                2 -> LEFT
                3 -> UP
                else -> throw IllegalArgumentException("Invalid direction: $number")
            }
        }
    }
}

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val instructions = input.split("\r\n")

        // Puzzle 1
        val (vertices1, length1) = parseVerticesAndLength1(instructions)
        println("Puzzle 1: ${area(vertices1, length1).toLong()}")

        // Puzzle 2
        val (vertices2, length2) = parseVerticesAndLength2(instructions)
        println("Puzzle 2: ${area(vertices2, length2).toLong()}")
    }
    println("Duration: $duration ms")
}

fun parseVerticesAndLength1(instructions: List<String>): Pair<List<Coordinate>, Long> {
    var length = 0L
    val vertices = mutableListOf(Coordinate(0, 0))
    instructions.forEach { instruction ->
        val (direction, amount) = instruction.split(" ")
        vertices.addAll(digRange(vertices.last(), Direction.parse(direction[0]), amount.toInt()))
        length += amount.toInt()
    }
    return vertices to length
}

@OptIn(ExperimentalStdlibApi::class)
fun parseVerticesAndLength2(instructions: List<String>): Pair<List<Coordinate>, Long> {
    var length = 0L
    val vertices = mutableListOf(Coordinate(0, 0))
    instructions.forEach { instruction ->
        val code = instruction.split("#")[1]
        val amount = code.take(5).hexToInt()
        val direction = code.drop(5).take(1).hexToInt()
        vertices.addAll(digRange(vertices.last(), Direction.parse(direction), amount))
        length += amount
    }
    return vertices to length
}

fun digRange(start: Coordinate, direction: Direction, amount: Int): List<Coordinate> {
    return when (direction) {
        Direction.UP -> {
            (start.y - amount..start.y).map { Coordinate(start.x, it) }.reversed()
        }

        Direction.DOWN -> {
            (start.y..start.y + amount).map { Coordinate(start.x, it) }
        }

        Direction.LEFT -> {
            (start.x - amount..start.x).map { Coordinate(it, start.y) }.reversed()
        }

        Direction.RIGHT -> {
            (start.x..start.x + amount).map { Coordinate(it, start.y) }
        }
    }
}

fun area(vertices: List<Coordinate>, length: Long): Double {
    return shoelaceArea(vertices) - length / 2 + 1 + length
}

fun shoelaceArea(vertices: List<Coordinate>): Double {
    var sum = 0L

    for(i in vertices.indices) {
        val a = vertices[i]
        val b = vertices[(i + 1) % vertices.size]

        sum += a.x * b.y - a.y * b.x
    }

    return 0.5 * abs(sum)
}