import kotlin.system.measureTimeMillis

data class Node(val name: String, val leftName: String, val rightName: String)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val (steps, nodes) = parseInput(input)

        // Puzzle 1
        val stepsCount = stepsCount(nodes["AAA"]!!, setOf("ZZZ"), steps, nodes)
        println("Puzzle 1: $stepsCount")

        // Puzzle 2
        val startNodes = nodes.filter { it.key.endsWith('A') }.values
        val endNodenames = nodes.filter { it.key.endsWith('Z') }.values.map { it.name }.toSet()
        val loopCounts = startNodes.map { stepsCount(it, endNodenames, steps, nodes).toLong() }
        println("Puzzle 2: ${loopCounts.fold(1L) { x, y -> lcm(x, y) }}")
    }

    println("Duration: $duration ms")
}

fun parseInput(input: String): Pair<String, Map<String, Node>> {
    val rows = input.split("\r\n")
    val steps = rows.first()

    val nodeRows = rows.drop(2)
    val nodes = nodeRows.associate {
        val name = it.take(3)
        val leftName = it.drop(7).take(3)
        val rightName = it.drop(12).take(3)
        name to Node(name, leftName, rightName)
    }

    return Pair(steps, nodes)
}

fun takeAStep(step: Char, node: Node, nodes: Map<String, Node>): Node {
    return when (step) {
        'L' -> nodes[node.leftName]!!
        'R' -> nodes[node.rightName]!!
        else -> throw IllegalArgumentException("Unknown step: $step")
    }
}

fun stepsCount(startNode: Node, endNodes: Set<String>, steps: String, nodes: Map<String, Node>): Int {
    var stepNumber = 0
    var atTheEnd = false
    var currentNode = startNode
    while (!atTheEnd) {
        val stepIndex = stepNumber % steps.length
        val step = steps[stepIndex]
        currentNode = takeAStep(step, currentNode, nodes)
        stepNumber++

        if (endNodes.contains(currentNode.name)) {
            atTheEnd = true
        }
    }
    return stepNumber
}

private fun gcd(x: Long, y: Long): Long {
    return if (y == 0L) x else gcd(y, x % y)
}

fun lcm(a: Long, b: Long): Long {
    return a * b / (gcd(a, b))
}