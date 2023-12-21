import java.util.*
import kotlin.system.measureTimeMillis

enum class Pulse {
    LOW,
    HIGH
}

enum class FlipFlopState {
    ON,
    OFF;

    fun flip() =
        when (this) {
            ON -> OFF
            OFF -> ON
        }
}

enum class ModuleType {
    BROADCASTER,
    FLIP_FLOP,
    CONJUNCTION,
    OUTPUT
}

data class Module(val name: String, val type: ModuleType, val outputs: List<String>)
data class State(val module: Module, val pulse: Pulse, val previousModuleName: String)

var modules: MutableMap<String, Module> = mutableMapOf()
val conjunctionMemories = mutableMapOf<String, MutableMap<String, Pulse>>()
val flipFlopStates = mutableMapOf<String, FlipFlopState>()

var lowPulsesCount = 0L
var highPulsesCount = 0L
var observedHighPulseModules = mutableMapOf<String, Pulse>()

fun main() {
    val duration = measureTimeMillis {
        val input = {}::class.java.getResource("input.txt").readText()
        val rows = input.split("\r\n")

        modules = parseModules(rows)
        initializeConjuntionMemories()
        initializeFlipFlopMemories()
        repeat(1000) { sendLowPulse("broadcaster") }

        // Puzzle 1
        println("Puzzle 1: ${lowPulsesCount * highPulsesCount}")

        // Puzzle 2
        initializeConjuntionMemories()
        initializeFlipFlopMemories()

        // rx is preceded by hf conjunction so well observe when all
        // outputs connected to that send a high pulse
        val hfConnectedMap =
            modules.filter { it.value.outputs.contains("hf") }.values.associate { it.name to -1L }.toMutableMap()
        repeat(10000) { pushNumber ->
            sendLowPulse("broadcaster", hfConnectedMap.keys.toSet())

            hfConnectedMap.keys.forEach {
                if (hfConnectedMap[it] == -1L && observedHighPulseModules[it] == Pulse.HIGH) {
                    hfConnectedMap[it] = pushNumber.toLong() + 1
                }
            }
        }
        println("Puzzle 2: ${hfConnectedMap.values.fold(1L) { x, y -> lcm(x, y) }}")
    }
    println("Duration: $duration ms")
}

fun sendLowPulse(firstModuleName: String, observedModuleNames: Set<String> = emptySet()) {
    val stateQueue: Queue<State> = LinkedList()

    stateQueue.add(State(getOrPutModule(firstModuleName), Pulse.LOW, "button"))

    while (stateQueue.isNotEmpty()) {
        val state = stateQueue.remove()

        when (state.pulse) {
            Pulse.LOW -> lowPulsesCount++
            Pulse.HIGH -> highPulsesCount++
        }

        if (observedModuleNames.contains(state.previousModuleName)) {
            if (state.pulse == Pulse.HIGH) {
                observedHighPulseModules[state.previousModuleName] = state.pulse
            }
        }

        when (state.module.type) {
            ModuleType.BROADCASTER -> {
                state.module.outputs.forEach {
                    stateQueue.add(
                        State(
                            getOrPutModule(it),
                            state.pulse,
                            state.module.name
                        )
                    )
                }
            }

            ModuleType.FLIP_FLOP -> {
                if (state.pulse == Pulse.LOW) {
                    val newState = flipFlopStates[state.module.name]!!.flip()
                    flipFlopStates[state.module.name] = newState

                    if (newState == FlipFlopState.ON) {
                        state.module.outputs.forEach {
                            stateQueue.add(
                                State(
                                    getOrPutModule(it),
                                    Pulse.HIGH,
                                    state.module.name
                                )
                            )
                        }
                    } else {
                        state.module.outputs.forEach {
                            stateQueue.add(
                                State(
                                    getOrPutModule(it),
                                    Pulse.LOW,
                                    state.module.name
                                )
                            )
                        }
                    }
                }
            }

            ModuleType.CONJUNCTION -> {
                conjunctionMemories[state.module.name]!![state.previousModuleName] = state.pulse

                if (conjunctionMemories[state.module.name]!!.all { it.value == Pulse.HIGH }) {
                    state.module.outputs.forEach {
                        stateQueue.add(State(getOrPutModule(it), Pulse.LOW, state.module.name))
                    }
                } else {
                    state.module.outputs.forEach {
                        stateQueue.add(State(getOrPutModule(it), Pulse.HIGH, state.module.name))
                    }
                }
            }

            ModuleType.OUTPUT -> {
                // Do nothing
            }
        }
    }
}

fun getOrPutModule(name: String) = modules.getOrPut(name) { Module(name, ModuleType.OUTPUT, emptyList()) }

fun parseModules(rows: List<String>) =
    rows.associate { row ->
        val (typeAndName, outputs) = row.split(" -> ")

        when (typeAndName) {
            "broadcaster" -> typeAndName to Module(typeAndName, ModuleType.BROADCASTER, outputs.split(", "))
            else -> {
                val type = typeAndName[0]
                val name = typeAndName.substring(1)
                when (type) {
                    '%' -> name to Module(name, ModuleType.FLIP_FLOP, outputs.split(", "))
                    '&' -> name to Module(name, ModuleType.CONJUNCTION, outputs.split(", "))
                    else -> typeAndName to Module(typeAndName, ModuleType.OUTPUT, emptyList())
                }
            }
        }
    }.toMutableMap()

fun initializeConjuntionMemories() {
    modules
        .filter { it.value.type == ModuleType.CONJUNCTION }
        .forEach { module ->
            val name = module.key
            conjunctionMemories[name] = mutableMapOf()
            modules
                .filter { it.value.outputs.contains(name) }
                .forEach {
                    conjunctionMemories[name]!![it.key] = Pulse.LOW
                }
        }
}

fun initializeFlipFlopMemories() {
    modules.filter { it.value.type == ModuleType.FLIP_FLOP }
        .forEach { module ->
            val name = module.key
            flipFlopStates[name] = FlipFlopState.OFF
        }
}

private fun gcd(x: Long, y: Long): Long {
    return if (y == 0L) x else gcd(y, x % y)
}

fun lcm(a: Long, b: Long): Long {
    return a * b / (gcd(a, b))
}
