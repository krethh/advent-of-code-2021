package days.day13

import java.io.IOException
import java.lang.Math.abs
import java.nio.file.Files
import java.nio.file.Path

object Day13 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        var dots = items.filter { !it.startsWith("fold along") }.filter { !it.isBlank() }.map {
            val parts = it.split(",")
            Pair(parts[0].toInt(), parts[1].toInt())
        }.toSet()

        val foldInstructions = items.filter { it.startsWith("fold along") }
            .map {
                val parts = it.split("\\s+".toRegex())[2].split("=")
                if (parts[0].contains("x")) {
                    return@map Pair("x", parts[1].toInt())
                } else {
                    return@map Pair("y", parts[1].toInt())
                }
            }

        println (reflectAlongX(dots, 655).size)

        foldInstructions.forEach {
            dots = if (it.first == "x") {
                reflectAlongX(dots, it.second)
            } else {
                reflectAlongY(dots, it.second)
            }
        }

        val maxY = dots.maxOf { it.second }

        for (j in 0..maxY) {
            val thisLine = dots.filter { it.second == j }
            for (k in 0..thisLine.maxOf { it.first }) {
                if (Pair(k, j) in dots) {
                    print("*")
                } else {
                    print(" ")
                }
            }
            println("")
        }

    }

    private fun reflectAlongX(dots: Set<Pair<Int, Int>>, coordinate: Int): Set<Pair<Int, Int>> {
        return dots.map {
            if (it.first < coordinate) {
                return@map it
            }
            val distanceFromCoordinate = abs(coordinate - it.first)
            return@map Pair(it.first - 2 * distanceFromCoordinate, it.second)
        }.toSet()
    }

    private fun reflectAlongY(dots: Set<Pair<Int, Int>>, coordinate: Int): Set<Pair<Int, Int>> {
        return dots.map {
            if (it.second < coordinate) {
                return@map it
            }
            val distanceFromCoordinate = abs(coordinate - it.second)
            return@map Pair(it.first, it.second - 2 * distanceFromCoordinate)
        }.toSet()
    }
}