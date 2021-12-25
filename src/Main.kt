fun main () {
    val totalExecutionTime = IntRange(1, 25).sumOf { solveDay(it) }
    println ("")
    println ("Total execution time of Advent of Code 2021: ${totalExecutionTime}s")
}