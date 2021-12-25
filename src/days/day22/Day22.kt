package days.day22

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day22 {

    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val cubes = items.map {
            val digitInts = "-?[1-9]\\d*|0".toRegex().findAll(it).map { it.value.toInt() }.toList()
            Cube(digitInts[0], digitInts[1], digitInts[2], digitInts[3], digitInts[4], digitInts[5], it.startsWith("on"))
        }

        solveCubes(cubes.filter { abs(it.x1) <= 50 })
        solveCubes(cubes)
    }

    fun solveCubes(cubes: List<Cube>) {
        val offCubes = mutableListOf<Cube>()
        val onCubes = mutableListOf<Cube>()

        val sum = cubes.sumOf {
            val onOverlaps = onCubes.mapNotNull { cube -> cube.getOverlapWith(it) }
            val offOverlaps = offCubes.mapNotNull { cube -> cube.getOverlapWith(it) }

            offCubes.addAll(onOverlaps)
            onCubes.addAll(offOverlaps)

            if (it.on) {
                onCubes.add(it)
            }

            offOverlaps.sumOf { it.size() } - onOverlaps.sumOf { it.size() } + it.size()
        }

        println(sum)
    }

    fun Cube.size(): Long {
        return if (this.on) (this.x2 - this.x1 + 1).toLong()
            .times((this.y2 - this.y1 + 1).toLong())
            .times((this.z2 - this.z1 + 1).toLong()) else 0L
    }

    private fun Cube.getOverlapWith(other: Cube): Cube? {
        val startOverlapX = max(this.x1, other.x1)
        val endOverlapX = min(this.x2, other.x2)
        val isOverlapX = startOverlapX <= endOverlapX

        val startOverlapY = max(this.y1, other.y1)
        val endOverlapY = min(this.y2, other.y2)
        val isOverlapY = startOverlapY <= endOverlapY

        val startOverlapZ = max(this.z1, other.z1)
        val endOverlapZ = min(this.z2, other.z2)
        val isOverlapZ = startOverlapZ <= endOverlapZ

        if (!(isOverlapX && isOverlapY && isOverlapZ)) {
            return null
        }
        return Cube(startOverlapX, endOverlapX, startOverlapY, endOverlapY, startOverlapZ, endOverlapZ, true)
    }

    data class Cube(val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int, val on: Boolean)
}