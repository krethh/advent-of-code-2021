package utils

fun <T> Iterable<T>.combinations(size: Int): List<Set<T>> {
    val result = mutableListOf<List<T>>()
    combinationsUtil(size, listOf(), result)
    return result.map { it.toSet() }.distinct()
}

private fun <T> Iterable<T>.combinationsUtil(size: Int, current: List<T>, result: MutableList<List<T>>) {
    if (current.size == size) {
        result.add(current)
        return
    }
    this.filter { !current.contains(it) }.forEach {
        combinationsUtil(size, current.plus(it), result)
    }
}