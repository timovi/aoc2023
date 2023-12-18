import java.util.*
import kotlin.system.measureTimeMillis

data class Coordinate(
    val x: Int,
    val y: Int
)

data class Node(
    val coordinate: Coordinate,
    val direction: Direction,
    val singleDirectionCount: Int,
)

data class State(
    val node: Node,
    val previous: State?,
    val distance: Int
) : Comparable<State> {
    override fun compareTo(other: State): Int {
        var ret = this.distance - other.distance

        if (ret == 0 && this.node.direction == other.node.direction) {
            ret = this.node.singleDirectionCount - other.node.singleDirectionCount
        }

        if (ret == 0) {
            ret =
                other.node.coordinate.y * 1000 + other.node.coordinate.x - this.node.coordinate.y * 1000 + this.node.coordinate.x
        }

        return ret
    }
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

var map = listOf<List<Int>>()

fun main() {
    val input = {}::class.java.getResource("input.txt").readText()
    map = input.split("\r\n").map { row -> row.map { it.digitToInt() } }

    // Puzzle 1
    val duration1 = measureTimeMillis {
        println("Puzzle 1: ${shortestPathWithDijkstra(State::next)}")
    }
    println("Duration: $duration1 ms")

    // Puzzle 2
    val duration2 = measureTimeMillis {
        println("Puzzle 2: ${shortestPathWithDijkstra(State::nextUltra, 4)}")
    }
    println("Duration: $duration2 ms")
}

fun shortestPathWithDijkstra(nextStatesFn: (State) -> List<State>, minStraightAtEnd: Int = 0): Int {
    val visited = mutableSetOf<Node>()
    val queue = PriorityQueue<State>()

    queue.add(State(Node(Coordinate(1, 0), Direction.RIGHT, 1), null, map[0][1]))
    queue.add(State(Node(Coordinate(0, 1), Direction.DOWN, 1), null, map[1][0]))

    while (queue.any()) {
        val current = queue.remove()
        if (!visited.contains(current.node)) {
            visited.add(current.node)

            if (current.node.isEnd() && current.node.singleDirectionCount >= minStraightAtEnd) {
                //printPath(current)
                return current.distance
            }

            queue.addAll(nextStatesFn(current))
        }
    }
    return 0
}

fun printPath(state: State) {
    val path = mutableListOf<State>()
    var current: State? = state
    while (current != null) {
        path.add(current)
        current = current.previous
    }
    path.reversed().forEach { println("${it.node} + dist ${it.distance}") }
}

fun State.next(): List<State> {
    val nodes = this.node.next().filter { it.singleDirectionCount <= 3 }

    return nodes.map {
        val newDistance = this.distance + map[it.coordinate.y][it.coordinate.x]
        State(it, this, newDistance)
    }
}

fun State.nextUltra(): List<State> {
    val allNodes = this.node.next()
    val nodes = when (this.node.singleDirectionCount) {
        in 1..3 -> allNodes.filter { it.singleDirectionCount > 1 }
        in 4..9 -> allNodes
        else -> allNodes.filter { it.singleDirectionCount == 1 }
    }

    return nodes.map {
        val newDistance = this.distance + map[it.coordinate.y][it.coordinate.x]
        State(it, this, newDistance)
    }
}

fun Node.next(): List<Node> {
    val up = Coordinate(this.coordinate.x, this.coordinate.y - 1)
    val right = Coordinate(this.coordinate.x + 1, this.coordinate.y)
    val down = Coordinate(this.coordinate.x, this.coordinate.y + 1)
    val left = Coordinate(this.coordinate.x - 1, this.coordinate.y)

    return when (this.direction) {
        Direction.UP -> listOf(
            Node(left, Direction.LEFT, 1),
            Node(up, Direction.UP, singleDirectionCount + 1),
            Node(right, Direction.RIGHT, 1)
        )

        Direction.RIGHT -> listOf(
            Node(up, Direction.UP, 1),
            Node(right, Direction.RIGHT, singleDirectionCount + 1),
            Node(down, Direction.DOWN, 1)
        )

        Direction.DOWN -> listOf(
            Node(left, Direction.LEFT, 1),
            Node(down, Direction.DOWN, singleDirectionCount + 1),
            Node(right, Direction.RIGHT, 1)
        )

        Direction.LEFT -> listOf(
            Node(up, Direction.UP, 1),
            Node(left, Direction.LEFT, singleDirectionCount + 1),
            Node(down, Direction.DOWN, 1)
        )
    }.filter { it.coordinate.isInsideMap() }
}

fun Coordinate.isInsideMap() = this.x >= 0 && this.y >= 0 && this.x < map[0].size && this.y < map.size

fun Node.isEnd() = this.coordinate == Coordinate(map[0].size - 1, map.size - 1)
