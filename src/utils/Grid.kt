package utils

class Grid<T> {

    lateinit var items: MutableList<GridElement<T>>
    var iSize: Int = 0
    var jSize: Int = 0

    fun at(i: Int, j: Int): GridElement<T>? {
        return items.firstOrNull { it.i == i && it.j == j }
    }

    fun valueAt(i: Int, j: Int, defaultValue: T): T {
        return items.firstOrNull { it.i == i && it.j == j }?.value ?: defaultValue
    }

    fun isWithin(i: Int, j: Int): Boolean = i < iSize && j < jSize && i >= 0 && j >= 0

    fun neighborsOf(i: Int, j: Int): List<GridElement<T>> {
        val indices = listOf(Pair(i, j - 1), Pair(i, j + 1), Pair(i + 1, j), Pair(i - 1, j),
            Pair(i + 1, j + 1), Pair(i - 1, j - 1), Pair(i + 1, j - 1), Pair(i - 1, j + 1))

        return indices.filter { isWithin(it.first, it.second) }.map { at(it.first, it.second)!! }
    }

    fun get3By3Square(i: Int, j: Int, defaultValue: T): List<T> {
//        val indexes = listOf(Pair(i-1, j - 1), Pair(i, j - 1), Pair(i + 1, j - 1), Pair(i - 1, j),
//            Pair(i, j), Pair(i + 1, j), Pair(i - 1, j + 1), Pair(i, j + 1), Pair(i + 1, j + 1)

        val indexes = listOf(Pair(i-1, j - 1), Pair(i - 1, j), Pair(i -1, j + 1), Pair(i, j -1 ),
            Pair(i, j), Pair(i, j + 1), Pair(i + 1, j - 1), Pair(i + 1, j), Pair(i + 1, j + 1))

        return indexes.map {
            if (!this.isWithin(it.first, it.second)) {
                defaultValue
            } else {
                valueAt(it.first, it.second, defaultValue)
            }
        }
    }

    fun horizontalVerticalNeighborsOf(i: Int, j: Int): List<GridElement<T>> {
        val indices = listOf(Pair(i, j - 1), Pair(i, j + 1), Pair(i + 1, j), Pair(i - 1, j))

        return indices.filter { isWithin(it.first, it.second) }.map { at(it.first, it.second)!! }
    }

    fun diagonalNeighborsOf(i: Int, j: Int): List<GridElement<T>> {
        val indices = listOf(Pair(i + 1, j + 1), Pair(i - 1, j - 1), Pair(i + 1, j - 1), Pair(i - 1, j + 1))

        return indices.filter { isWithin(it.first, it.second) }.map { at(it.first, it.second)!! }
    }

    companion object {
        fun <T> from(input: List<String>, transformElement: (inputElement: String) -> T): Grid<T> {
            val grid = Grid<T>()

            val items = input.map {
                it.map { transformElement(it.toString()) }
            }

            val gridElements = mutableListOf<GridElement<T>>()

            for (i in items.indices) {
                for (j in items[i].indices) {
                    gridElements.add(GridElement(items[i][j], i, j))
                }
            }

            grid.items = gridElements
            grid.iSize = items.size
            grid.jSize = items[0].size
            return grid
        }
    }

}