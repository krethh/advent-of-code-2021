package days.day15

import utils.Grid
import utils.GridElement
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

        performDijkstra(grid)

        // ugly
        for (i in 0 until grid.iSize) {
            for (j in 0 until grid.jSize) {
                for (stepI in 0 until 5) {
                    for (stepJ in 0 until 5) {
                        if (!(stepI == 0 && stepJ == 0)) {
                            var newValue = (grid.valueAt(i, j) + stepI + stepJ)
                            if (newValue > 9) {
                                newValue %= 9
                            }
                            val newElement =
                                GridElement(value = newValue, i + grid.iSize * stepI, j + grid.jSize * stepJ)
                            grid.items.add(newElement)
                        }
                    }
                }
            }
        }

        grid.iSize *= 5
        grid.jSize *= 5

        performDijkstra(grid)
    }

    private fun performDijkstra(grid: Grid<Int>) {
        var current = Pair(0, 0)
        val finalPosition = Pair(grid.iSize - 1, grid.jSize - 1)

        val riskToNode = mutableMapOf<Pair<Int, Int>, Int>()
        val comparator = compareBy<Pair<Int, Int>> { riskToNode.getOrDefault(it, Int.MAX_VALUE) }
        val queue = PriorityQueue(comparator)
        riskToNode[current] = 0
        queue.add(current)

        /**
         * Note to future self: Dijkstra is king. Don't try to write recursive functions if you don't need to, dumbass
         */
        while (!queue.isEmpty()) {
            current = queue.remove()
            if (current == finalPosition) {
                break
            }
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