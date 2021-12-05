package days.day5

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day5 {
    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val lines = Files.readAllLines(input).map { it ->
            var startPointParts = it.split("->")[0].split(",").map { it.trim() }
            var startPoint = Point(startPointParts[0].toInt(), startPointParts[1].toInt())

            var endPointParts = it.split("->")[1].split(",").map { it.trim() }
            var endPoint = Point(endPointParts[0].toInt(), endPointParts[1].toInt())

            Line(startPoint, endPoint)
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