package days.day5

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Day5 {
    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val lines = Files.readAllLines(input).map { it ->
            val parts = it.split("->").map { it.trim() }

            Line(Point(parts[0]), Point(parts[1]))
        }

        solvePartOne(lines)
        solvePartTwo(lines)
    }

    private fun solvePartTwo(lines: List<Line>) {
        val allPoints = lines.flatMap { it.getAllPointsCovered() }
        val grouped = allPoints.groupingBy { it }.eachCount()

        println(grouped.values.filter { it > 1 }.size)
    }

    private fun solvePartOne(lines: List<Line>) {
        val allPoints = lines.filter { it.isHorizontalOrVertical() }.flatMap { it.getAllPointsCovered() }
        val grouped = allPoints.groupingBy { it }.eachCount()

        println(grouped.values.filter { it > 1 }.size)
    }
}