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

data class Rule(
    val partCategory: Char?,
    val comparator: Comparator?,
    val comparatorValue: Int?,
    val ifTrue: String
)

data class Part(
    val categories: Map<Char, Int>
)

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        val workflows = parseWorkflows(rows)
        val parts = parseParts(rows)

        // Puzzle 1
        //workflows.forEach(::println)
        //parts.forEach(::println)

        val accepted = parts.filter { isAccepted(it, workflows, workflows["in"]!!) }
        println("Puzzle 1: ${accepted.sumOf { it.categories.values.sum() }}")

        // Puzzle 2
        println("Puzzle 2: ")
    }
    println("Duration: $duration ms")
}

fun isAccepted(part: Part, workflows: Map<String, List<Rule>>, currentWorkflow: List<Rule>): Boolean {
    for (rule in currentWorkflow) {
        if (rule.comparator == null && rule.accepts()) {
            return true
        }
        if (rule.comparator == null && rule.rejects()) {
            return false
        }
        if (rule.comparator == null) {
            val nextWorkflow = workflows[rule.ifTrue]!!
            return isAccepted(part, workflows, nextWorkflow)
        }

        val partValue = part.categories[rule.partCategory]!!
        val comparisonResult = rule.compareTo(partValue)

        if (comparisonResult && rule.accepts()) {
            return true
        }
        if (comparisonResult && rule.rejects()) {
            return false
        }
        if (comparisonResult) {
            val nextWorkflow = workflows[rule.ifTrue]!!
            return isAccepted(part, workflows, nextWorkflow)
        }
    }
    return false
}

fun parseWorkflows(rows: List<String>) =
    rows.takeWhile { it.isNotBlank() }
        .map(::parseWorkflow)
        .associate { (name, rules) -> name to rules }

fun parseParts(rows: List<String>) =
    rows.dropWhile { it.isNotBlank() }
        .drop(1)
        .map(::parsePart)

fun parseWorkflow(workflow: String): Pair<String, List<Rule>> {
    val (name, rest) = workflow.split("{")
    val rules = rest.dropLast(1).split(",").map{ rule -> parseRule(rule) }
    return name to rules
}

fun parseRule(rule: String): Rule {
    val destinationSeparatorIndex = rule.indexOf(":")
    return if (destinationSeparatorIndex != -1) {
        Rule(
            rule[0],
            Comparator.parse(rule[1]),
            rule.substring(2, destinationSeparatorIndex).toInt(),
            rule.substring(destinationSeparatorIndex + 1)
        )
    } else {
        Rule(null, null, null, rule)
    }
}

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

fun Rule.compareTo(partValue: Int): Boolean {
    return when (this.comparator) {
        Comparator.LESS_THAN -> partValue < this.comparatorValue!!
        Comparator.GREATER_THAN -> partValue > this.comparatorValue!!
        else -> throw IllegalArgumentException("Unknown comparator: $comparator")
    }
}
fun Rule.accepts() = this.ifTrue == "A"
fun Rule.rejects() = this.ifTrue == "R"