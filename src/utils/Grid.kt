package utils

class Grid<T> {

    lateinit var items: MutableList<GridElement<T>>
    var iSize: Int = 0
    var jSize: Int = 0

    fun copy(): Grid<T> {
        val newItems = mutableListOf<GridElement<T>>()
        items.forEach { newItems.add(GridElement(it.value, it.i, it.j)) }
        val grid = Grid<T>()
        grid.items = items
        grid.iSize = this.iSize
        grid.jSize = this.jSize

        return grid
    }

    fun at(i: Int, j: Int): GridElement<T>? {
        return items.firstOrNull { it.i == i && it.j == j }
    }

    fun valueAt(i: Int, j: Int, defaultValue: T): T {
        return items.firstOrNull { it.i == i && it.j == j }?.value ?: defaultValue
    }

    fun swap(i1: Int, j1: Int, i2: Int, j2: Int): Grid<T> {
        val newGrid = this.copy()

        val first = this.at(i1, j1)!!.value
        newGrid.at(i1, j1)!!.value = this.at(i2, j2)!!.value
        newGrid.at(i2, j2)!!.value = first

        return newGrid
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

    fun print() {
        for (i in 0 until iSize) {
            for (j in 0 until jSize) {
                print(this.at(i, j)!!.value)
            }
            println()
        }
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