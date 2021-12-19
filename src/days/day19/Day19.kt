package days.day19

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.max

object Day19 {

    @JvmStatic
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

        var pointId = 0
        groupedByScanner.forEach { scanner, points ->
            points.forEach {
                it.id = pointId++
                it.originalId = it.id
            }
        }

        val normalizedScannersMap = mutableMapOf<Int, Boolean>()
        normalizedScannersMap[0] = true

        val scannerPositions = mutableListOf<Point>()
        var beacons = mutableSetOf<Point>()
        beacons.addAll(groupedByScanner[0]!!.map { Point(it.x, it.y, it.z) })
        while (true) {
            if (normalizedScannersMap.filter { it.value }.size == groupedByScanner.size) {
                break
            }
            val notNormalizedScanners = groupedByScanner.filter { !normalizedScannersMap.containsKey(it.key) }.keys

            for (notNormalizedScanner in notNormalizedScanners) {
                val crossingResult = checkForBeacons(beacons.toList(), groupedByScanner[notNormalizedScanner]!!)
                if (crossingResult.atLeast12Match) {
                    scannerPositions.add(Point(crossingResult.offset!!.x, crossingResult.offset.y, crossingResult.offset.z))
                    val normalizedPoints = crossingResult.winningPermutation!!.map { it.shift(crossingResult.offset!!) }
                    beacons.addAll(normalizedPoints.map { Point(it.x, it.y, it.z) })
                    normalizedScannersMap[notNormalizedScanner!!] = true
                }
            }
        }

        println(beacons.size)
        var maxManhattanDistance = 0
        scannerPositions.forEach { scanner ->
            scannerPositions.filter { it != scanner }.forEach {
                maxManhattanDistance = max(maxManhattanDistance, scanner.manhattanDistanceTo(it))
            }
        }
        println(maxManhattanDistance)
    }

    private fun Point.manhattanDistanceTo(other: Point): Int {
        return abs(other.x - this.x) + abs(other.y - this.y) + abs(other.z - this.z)
    }

    private fun Point.shift(offset: Offset): Point {
        return Point(
            x + offset.x,
            y + offset.y,
            z + offset.z,
            this.scanner,
            this.permutationId,
            this.id,
            this.originalId,
            this.sourcePoint
        )
    }

    private fun checkForBeacons(firstScanner: List<Point>, secondScanner: List<Point>): CrossingResult {
        val permutations = secondScanner.flatMap { it.permutations() }.groupBy { it.permutationId }
        for (permutation in permutations) {
            val firstOffsets = getPairwiseOffsets(firstScanner)
            val secondOffsets = getPairwiseOffsets(permutation.value)

            val beacons1 = getBeaconsFromOffsets(firstOffsets, secondOffsets)
            if ((beacons1?.size ?: 0) >= 12) {
                val beacons2 = getBeaconsFromOffsets(secondOffsets, firstOffsets)
                if ((beacons2?.size ?: 0) >= 12) {
                    val offset = getOffset(beacons1!!, beacons2!!)
                    if (offset != Offset(0, 0, 0)) {
                        return CrossingResult(true, permutation.value.toSet(), offset)
                    }
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

    private fun getOffset(firstPoints: List<Point?>, secondPoints: List<Point?>): Offset {
        var allXOffsets = mutableListOf<Int>()
        var allYOffsets = mutableListOf<Int>()
        var allZOffsets = mutableListOf<Int>()

        firstPoints.forEach { first ->
            secondPoints.forEach { second ->
                allXOffsets.add(first!!.x - second!!.x)
                allYOffsets.add(first.y - second.y)
                allZOffsets.add(first.z - second.z)
            }
        }

        // is there a most common element?
        val xOffsetsGrouped = allXOffsets.groupingBy { it }.eachCount()
        val yOffsetsGrouped = allYOffsets.groupingBy { it }.eachCount()
        val zOffsetsGrouped = allZOffsets.groupingBy { it }.eachCount()
        if (xOffsetsGrouped.values.any { it > 11 } && yOffsetsGrouped.values.any { it > 11 } && zOffsetsGrouped.values.any { it > 11 }) {
            val mostCommonX = xOffsetsGrouped.maxByOrNull { it.value }!!.key
            val mostCommonY = allYOffsets.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key
            val mostCommonZ = allZOffsets.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key
            val offset = Offset(mostCommonX, mostCommonY, mostCommonZ)
            return offset
        }
        return Offset(0, 0, 0)
    }

    private fun getPairwiseOffsets(points: List<Point?>): List<Offset> {
        val result = mutableListOf<Offset>()
        for (i in points.indices)
            for (j in points.indices) {
                if (i != j) {
                    result.add(
                        Offset(
                            points[i]!!.x - points[j]!!.x,
                            points[i]!!.y - points[j]!!.y,
                            points[i]!!.z - points[j]!!.z,
                            points[i],
                            points[j]
                        )
                    )
                }
            }
        return result
    }

    private fun Point.permutations(): List<Point> {
        val x = this.x
        val y = this.y
        val z = this.z

        return listOf(
            Point(x,y,z, scanner, 0, sourcePoint = this, originalId = id),
            Point(y,z,x, scanner, 1, sourcePoint = this, originalId = id),
            Point(z,x,y, scanner, 2, sourcePoint = this, originalId = id),
            Point(z,y,-x, scanner, 3, sourcePoint = this, originalId = id),
            Point(y,x,-z, scanner, 4, sourcePoint = this, originalId = id),
            Point(x,z,-y, scanner, 5, sourcePoint = this, originalId = id),
            Point(x,-y,-z, scanner, 6, sourcePoint = this, originalId = id),
            Point(y,-z,-x, scanner, 7, sourcePoint = this, originalId = id),
            Point(z,-x,-y, scanner, 8, sourcePoint = this, originalId = id),
            Point(z,-y,x, scanner, 9, sourcePoint = this, originalId = id),
            Point(y,-x,z, scanner, 10, sourcePoint = this, originalId = id),
            Point(x,-z,y, scanner, 11, sourcePoint = this, originalId = id),
            Point(-x,y,-z, scanner, 12, sourcePoint = this, originalId = id),
            Point(-y,z,-x, scanner, 13, sourcePoint = this, originalId = id),
            Point(-z,x,-y, scanner, 14, sourcePoint = this, originalId = id),
            Point(-z,y,x, scanner, 15, sourcePoint = this, originalId = id),
            Point(-y,x,z, scanner, 16, sourcePoint = this, originalId = id),
            Point(-x,z,y, scanner, 17, sourcePoint = this, originalId = id),
            Point(-x,-y,z, scanner, 18, sourcePoint = this, originalId = id),
            Point(-y,-z,x, scanner, 19, sourcePoint = this, originalId = id),
            Point(-z,-x,y, scanner, 20, sourcePoint = this, originalId = id),
            Point(-z,-y,-x,scanner, 21, sourcePoint = this, originalId = id),
            Point(-y,-x,-z,scanner, 22, sourcePoint = this, originalId = id),
            Point(-x,-z,-y, scanner, 23, sourcePoint = this, originalId = id),
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

    data class Point(
        var x: Int,
        var y: Int,
        var z: Int,
        var scanner: Int? = 0,
        val permutationId: Int = 0,
        var id: Int = 0,
        var originalId: Int = 0,
        val sourcePoint: Point? = null
    )
}