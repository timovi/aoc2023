import kotlin.system.measureTimeMillis

data class Lens(val label: String, val focalLength: Int)
val boxes = mutableListOf<MutableList<Lens>>()

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val sequences = input.split(",")

        // Puzzle 1
        println("Puzzle 1: ${sequences.sumOf(::calculateHashOfSequence)}")

        // Puzzle 2
        (0..255).forEach { _ -> boxes.add(mutableListOf()) }
        sequences.forEach(::followInstruction)
        println("Puzzle 2: ${calculateFocusingPowers()}")
    }

    println("Duration: $duration ms")
}

fun followInstruction(instruction: String) {
    if (instruction.contains('=')) {
        val (lensLabel, focalLength) = instruction.split('=')
        val lens = Lens(lensLabel, focalLength.toInt())
        val hash = calculateHashOfSequence(lensLabel)
        val box = boxes[hash]
        when (val index = box.indexOfFirst { it.label == lensLabel }) {
            -1 -> box.add(lens)
            else -> box[index] = lens
        }
    } else {
        val lensLabel = instruction.split('-').first()
        val hash = calculateHashOfSequence(lensLabel)
        boxes[hash].removeIf { it.label == lensLabel }
    }
}

fun calculateFocusingPowers() =
    boxes.mapIndexed { boxIndex, box ->
        box.mapIndexed { lensIndex, lens ->
            (boxIndex + 1) * (lensIndex + 1) * lens.focalLength
        }.sum()
    }.sum()

fun calculateHashOfSequence(input: String) =
    input.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

