package days.day22

import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day22 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        var cubes = mutableListOf<Cube>()
        items.forEach {
            val pattern = Pattern.compile("-?[1-9]\\d*|0")
            val matcher = pattern.matcher(it)
            val digits = mutableListOf<String>()
            while (matcher.find()) {
                digits.add(matcher.group())
            }
            val digitInts = digits.map { it.toInt() }

            val startX = min(digitInts[0], digitInts[1])
            val endX = max(digitInts[1], digitInts[0])
            val startY = min(digitInts[2], digitInts[3])
            val endY = max(digitInts[2], digitInts[3])
            val startZ = min(digitInts[4], digitInts[5])
            val endZ = max(digitInts[4], digitInts[5])

            cubes.add(Cube(startX, endX, startY, endY, startZ, endZ, it.startsWith("on")))
        }

        val totalPoints = mutableSetOf<Point>()

        cubes.filter { abs(it.startX) < 50 }.forEach {
            val points = it.toPoints()
            if (it.on) {
                totalPoints.addAll(points)
            } else {
                totalPoints.removeAll(points)
            }
        }

        println(totalPoints.size)

    }

    fun Cube.size(): BigDecimal {
        return (this.endX - this.startX + 1).toBigDecimal().times((this.endY - this.startY + 1).toBigDecimal()).times((this.endZ - this.startZ + 1).toBigDecimal())
    }

    fun Cube.sizeOfOverlapWith(other: Cube): BigDecimal {
        var startOverlapX = max(this.startX, other.startX)
        var endOverlapX = (min(this.endX, other.endX))
        
        var overlapX = startOverlapX < endOverlapX
        
        var startOverlapY = max(this.startY, other.startY)
        var endOverlapY = min(this.endY, other.endY)
        
        var overlapY = startOverlapY < endOverlapY  
        
        var startOverlapZ = max(this.startZ, other.startZ)
        var endOverlapZ = min(this.endZ, other.endZ)
        
        var overlapZ = startOverlapZ < endOverlapZ

        if (!(overlapX && overlapY && overlapZ)) {
            return BigDecimal.ZERO
        }
        return Cube(startOverlapX, endOverlapX, startOverlapY, endOverlapY, startOverlapZ, endOverlapZ, true).size()
    }

    fun Cube.toPoints(): Set<Point> {
        val result = mutableSetOf<Point>()
        for (i in this.startX..this.endX) {
            for (j in this.startY..this.endY) {
                for (k in this.startZ..this.endZ) {
                    result.add(Point(i, j, k))
                }
            }
        }
        return result
    }

    data class Point(val x: Int, val y: Int, val z: Int)
    data class Cube(val startX: Int, val endX: Int, val startY: Int, val endY: Int, val startZ: Int, val endZ: Int, val on: Boolean)
}