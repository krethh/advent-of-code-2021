package days.day2

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day2 {
    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        solvePartOne(items)
        solvePartTwo(items)

    }

    private fun solvePartTwo(items: List<String>) {
        var aim = 0;
        var horizontalPosition = 0;
        var depth = 0;

        items.forEach { action ->
            val value = action.split("\\s+".toRegex())[1].toInt()

            if (action.startsWith("forward")) {
                horizontalPosition += value
                depth += aim * value
            }
            if (action.startsWith("down")) {
                aim += value
            }
            if (action.startsWith("up")) {
                aim -= value
            }
        }

        println (horizontalPosition * depth)

    }

    fun solvePartOne(items: List<String>) {
        var depth = 0;
        var horizontalPosition = 0;

        items.forEach { action ->
            val value = action.split("\\s+".toRegex())[1].toInt()

            if (action.startsWith("forward")) {
                horizontalPosition += value
            }
            if (action.startsWith("down")) {
                depth += value
            }
            if (action.startsWith("up")) {
                depth -= value
            }
        }

        println(depth * horizontalPosition)
    }
}