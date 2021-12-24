package days.day19

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs

class Day19 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }.filter { it.isNotBlank() }
        val points = mutableListOf<Point>()
        var currentScanner = -1
        for (item in items) {
            if (item.contains("scanner")) {
                currentScanner++
            } else {
                val parts = item.split(",")
                points.add(Point(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), currentScanner))
            }
        }

        val groupedByScanner = points.groupBy { it.scanner }
        val normalizedScannersMap = mutableMapOf(0 to true)

        val scannerPositions = mutableListOf<Point>()
        val beacons = mutableSetOf<Point>()

        beacons.addAll(groupedByScanner[0]!!.map { Point(it.x, it.y, it.z) })
        while (true) {
            if (normalizedScannersMap.filter { it.value }.size == groupedByScanner.size) {
                break
            }
            val notNormalizedScanners = groupedByScanner.filter { !normalizedScannersMap.containsKey(it.key) }.keys

            for (notNormalizedScanner in notNormalizedScanners) {
                val crossingResult = checkForBeacons(beacons.toList(), groupedByScanner[notNormalizedScanner]!!)
                if (crossingResult.atLeast12Match) {
                    scannerPositions.add(
                        Point(
                            crossingResult.offset!!.x,
                            crossingResult.offset.y,
                            crossingResult.offset.z
                        )
                    )
                    val normalizedPoints = crossingResult.winningPermutation!!.map { it.shift(crossingResult.offset) }
                    beacons.addAll(normalizedPoints.map { Point(it.x, it.y, it.z) })
                    normalizedScannersMap[notNormalizedScanner!!] = true
                }
            }
        }

        println(beacons.size)
        val maxManhattanDistance = scannerPositions.flatMap { pos -> scannerPositions.map { it.manhattanDistanceTo(pos) } }.maxOrNull()
        println(maxManhattanDistance)
    }

    private fun Point.manhattanDistanceTo(other: Point): Int {
        return abs(other.x - this.x) + abs(other.y - this.y) + abs(other.z - this.z)
    }

    private fun Point.shift(offset: Offset): Point {
        return Point(x + offset.x, y + offset.y, z + offset.z, this.scanner, this.permutationId)
    }

    private fun checkForBeacons(existingBeacons: List<Point>, candidates: List<Point>): CrossingResult {
        val permutations = candidates.flatMap { it.permutations() }.groupBy { it.permutationId }
        for (permutation in permutations) {
            val firstOffsets = getPairwiseOffsets(existingBeacons)
            val secondOffsets = getPairwiseOffsets(permutation.value)

            val firstBeacons = getBeaconsFromOffsets(firstOffsets, secondOffsets)
            val secondBeacons = getBeaconsFromOffsets(secondOffsets, firstOffsets)
            if ((firstBeacons?.size ?: 0) >= 12 && (secondBeacons?.size ?: 0) >= 12) {
                val offset = getOffset(firstBeacons!!, secondBeacons!!)
                if (offset != null) {
                    return CrossingResult(true, permutation.value.toSet(), offset)
                }
            }
        }

        return CrossingResult(false, null, null)
    }

    private fun getBeaconsFromOffsets(firstOffsets: List<Offset>, secondOffsets: List<Offset>): List<Point?>? {
        val intersection = firstOffsets.intersect(secondOffsets.toSet()).groupBy { it.firstPoint }
        return intersection.maxByOrNull { it.value.size }?.value?.map { it.secondPoint }
            ?.plus(intersection.maxByOrNull { it.value.size }!!.key)
    }

    private fun getOffset(firstPoints: List<Point?>, secondPoints: List<Point?>): Offset? {
        val allXOffsets = mutableListOf<Int>()
        val allYOffsets = mutableListOf<Int>()
        val allZOffsets = mutableListOf<Int>()

        firstPoints.forEach { first ->
            secondPoints.filter { it != first }.forEach { second ->
                allXOffsets.add(first!!.x - second!!.x)
                allYOffsets.add(first.y - second.y)
                allZOffsets.add(first.z - second.z)
            }
        }

        val xOffsetsGrouped = allXOffsets.groupingBy { it }.eachCount()
        val yOffsetsGrouped = allYOffsets.groupingBy { it }.eachCount()
        val zOffsetsGrouped = allZOffsets.groupingBy { it }.eachCount()
        if (xOffsetsGrouped.values.any { it >= 12 } && yOffsetsGrouped.values.any { it >= 12 } && zOffsetsGrouped.values.any { it >= 12 }) {
            val mostCommonX = xOffsetsGrouped.maxByOrNull { it.value }!!.key
            val mostCommonY = yOffsetsGrouped.maxByOrNull { it.value }!!.key
            val mostCommonZ = zOffsetsGrouped.maxByOrNull { it.value }!!.key
            return Offset(mostCommonX, mostCommonY, mostCommonZ)
        }
        return null
    }

    private fun getPairwiseOffsets(points: List<Point?>): List<Offset> {
        return points.flatMap { outer ->
            points.filter { it != outer }.map { Offset(outer!!.x - it!!.x, outer.y - it.y, outer.z - it.z, outer, it) }
        }
    }

    private fun Point.permutations(): List<Point> {
        val x = this.x
        val y = this.y
        val z = this.z

        return listOf(
            Point(x, y, z, scanner, 0), Point(y, z, x, scanner, 1),
            Point(z, x, y, scanner, 2), Point(z, y, -x, scanner, 3),
            Point(y, x, -z, scanner, 4), Point(x, z, -y, scanner, 5),
            Point(x, -y, -z, scanner, 6), Point(y, -z, -x, scanner, 7),
            Point(z, -x, -y, scanner, 8), Point(z, -y, x, scanner, 9),
            Point(y, -x, z, scanner, 10), Point(x, -z, y, scanner, 11),
            Point(-x, y, -z, scanner, 12), Point(-y, z, -x, scanner, 13),
            Point(-z, x, -y, scanner, 14), Point(-z, y, x, scanner, 15),
            Point(-y, x, z, scanner, 16), Point(-x, z, y, scanner, 17),
            Point(-x, -y, z, scanner, 18), Point(-y, -z, x, scanner, 19),
            Point(-z, -x, y, scanner, 20), Point(-z, -y, -x, scanner, 21),
            Point(-y, -x, -z, scanner, 22), Point(-x, -z, -y, scanner, 23)
        )
    }

    data class Offset(
        val x: Int,
        val y: Int,
        val z: Int,
        val firstPoint: Point? = null,
        val secondPoint: Point? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Offset

            if (x != other.x) return false
            if (y != other.y) return false
            if (z != other.z) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            result = 31 * result + z
            return result
        }
    }

    data class CrossingResult(val atLeast12Match: Boolean, val winningPermutation: Set<Point>?, val offset: Offset?)
    data class Point(var x: Int, var y: Int, var z: Int, var scanner: Int? = 0, val permutationId: Int = 0)
}