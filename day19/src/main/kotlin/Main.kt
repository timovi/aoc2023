import kotlin.system.measureTimeMillis

enum class Comparator {
    LESS_THAN, GREATER_THAN;

    companion object {
        fun parse(char: Char): Comparator {
            return when (char) {
                '<' -> LESS_THAN
                '>' -> GREATER_THAN
                else -> throw IllegalArgumentException("Unknown comparator: $char")
            }
        }
    }
}

data class Part(
    val categories: Map<Char, Int>
)

data class Condition(
    val partCategory: Char,
    val comparator: Comparator,
    val comparatorValue: Int,
)

data class Node(
    val condition: Condition? = null,
    val ifTrue: Node? = null,
    val ifFalse: Node? = null,
    val isAccepted: Boolean? = null
)

val paths = mutableListOf<List<Node>>()

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        val workflows = parseWorkflows(rows)
        val parts = parseParts(rows)

        // Puzzle 1
        val accepted = parts.filter { isAccepted(it, workflows) }
        println("Puzzle 1: ${accepted.sumOf { it.categories.values.sum() }}")

        // Puzzle 2
        populateAcceptedPaths(workflows)
        println("Puzzle 2: ${rangeCombinations(paths)}")
    }
    println("Duration: $duration ms")
}

fun isAccepted(part: Part, node: Node): Boolean {
    if (node.isAccepted != null) {
        return node.isAccepted
    }

    if (node.condition != null) {
        val partValue = part.categories[node.condition.partCategory]!!
        val comparisonResult = node.condition.run(partValue)
        return if (comparisonResult) {
            isAccepted(part, node.ifTrue!!)
        } else {
            isAccepted(part, node.ifFalse!!)
        }
    }

    return isAccepted(part, node.ifTrue!!)
}

fun Condition.run(partValue: Int): Boolean {
    return when (this.comparator) {
        Comparator.LESS_THAN -> partValue < this.comparatorValue
        Comparator.GREATER_THAN -> partValue > this.comparatorValue
    }
}

fun parseWorkflows(rows: List<String>): Node {
    val workflowRows = rows.takeWhile { it.isNotBlank() }.associate(::parseWorkflow)
    val start = workflowRows["in"]!!
    return parseNode(start.first(), start.drop(1), workflowRows)
}

fun parseWorkflow(workflow: String): Pair<String, List<String>> {
    val (name, rest) = workflow.split("{")
    val rules = rest.dropLast(1).split(",")
    return name to rules
}

fun parseNode(rule: String, restRules: List<String>, workflows: Map<String, List<String>>): Node {
    if (rule == "A") {
        return Node(null, null, null, true)
    }

    if (rule == "R") {
        return Node(null, null, null, false)
    }

    if (!rule.contains(":")) {
        val nextWorkflow = workflows[rule]!!
        val nextNode = parseNode(nextWorkflow.first(), nextWorkflow.drop(1), workflows)
        return Node(null, nextNode, null, null)
    }

    val condition = Condition(rule[0], Comparator.parse(rule[1]), rule.substring(2, rule.indexOf(":")).toInt())
    val next = rule.substring(rule.indexOf(":") + 1)

    val nextNodeIfTrue = if (next == "A" || next == "R") {
        parseNode(next, restRules, workflows)
    } else {
        val nextWorkflow = workflows[next]!!
        parseNode(nextWorkflow.first(), nextWorkflow.drop(1), workflows)
    }
    val nextNodeIfFalse = parseNode(restRules.first(), restRules.drop(1), workflows)

    return Node(condition, nextNodeIfTrue, nextNodeIfFalse, null)
}

fun populateAcceptedPaths(node: Node, previousNodes: List<Node> = emptyList()) {
    if (node.ifTrue != null) {
        populateAcceptedPaths(node.ifTrue, previousNodes + node)
    }

    if (node.ifFalse != null) {
        val newComparatorAndValue = when (node.condition!!.comparator) {
            Comparator.LESS_THAN -> Comparator.GREATER_THAN to node.condition.comparatorValue - 1
            Comparator.GREATER_THAN -> Comparator.LESS_THAN to node.condition.comparatorValue + 1
        }
        val newNode = Node(
            Condition(node.condition.partCategory, newComparatorAndValue.first, newComparatorAndValue.second),
            node.ifTrue,
            node.ifFalse,
            node.isAccepted
        )
        populateAcceptedPaths(node.ifFalse, previousNodes + newNode)
    }

    if (node.isAccepted != null && node.isAccepted) {
        paths.add(previousNodes + node)
    }
}

fun rangeCombinations(acceptedPaths: List<List<Node>>): Long {
    val acceptedRangesForPaths = acceptedPaths.map(::acceptedRanges).filter { it.isNotEmpty() }
    return acceptedRangesForPaths.sumOf {
        it['x']!!.count().toLong() * it['m']!!.count() * it['a']!!.count() * it['s']!!.count()
    }
}

fun acceptedRanges(acceptedPath: List<Node>): Map<Char, IntRange> {
    val ranges = mutableMapOf(
        'x' to 1..4000,
        'm' to 1..4000,
        'a' to 1..4000,
        's' to 1..4000
    )

    var containsInvalidRange = false
    acceptedPath.dropLast(1).forEach { node ->
        if (node.condition != null) {
            val partCategory = node.condition.partCategory
            val comparatorValue = node.condition.comparatorValue
            val range = ranges[partCategory]!!
            val newRange = when (node.condition.comparator) {
                Comparator.LESS_THAN -> range.first..<comparatorValue
                Comparator.GREATER_THAN -> (comparatorValue + 1)..range.last
            }

            if (newRange.first > newRange.last) {
                containsInvalidRange = true
            }

            ranges[partCategory] = newRange
        }
    }

    return if (!containsInvalidRange) {
        ranges
    } else {
        emptyMap()
    }
}

fun parseParts(rows: List<String>) =
    rows.dropWhile { it.isNotBlank() }
        .drop(1)
        .map(::parsePart)

fun parsePart(part: String): Part {
    val categories = part
        .drop(1).dropLast(1)
        .split(",")
        .associate {
            val (key, value) = it.split("=")
            key.first() to value.toInt()
        }
    return Part(categories)
}