package days.day15

import utils.Grid
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object Day15 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val grid = Grid.from(items) { it.toInt() }
        val finalPosition = Pair(grid.iSize - 1, grid.jSize - 1)

        var current = Pair(0, 0)
        var visited = mutableSetOf<Pair<Int, Int>>()

        val queue = ArrayDeque<Pair<Int, Int>>()
        val riskToNode = mutableMapOf<Pair<Int, Int>, Int>()
        riskToNode.put(current, 0)
        queue.add(current)

        /**
         * Note to future self: Dijkstra is king. Don't try to write recursive functions if you don't need to, dumbass
         */
        while (!queue.isEmpty()) {
            current = queue.removeFirst()
            if (visited.contains(current)) {
                continue
            }
            if (current == finalPosition) {
                break
            }
            visited.add(current)
            val neighbors = grid.horizontalVerticalNeighborsOf(current.first, current.second)
            neighbors.forEach {
                val riskForNeighbor = riskToNode[current]!! + it.value
                if (riskToNode.getOrDefault(Pair(it.i, it.j), Int.MAX_VALUE) > riskForNeighbor) {
                    riskToNode[Pair(it.i, it.j)] = riskForNeighbor
                    queue.add(Pair(it.i, it.j))
                }
            }

        }

        println(riskToNode[finalPosition])
    }

}