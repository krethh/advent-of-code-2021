package days.day5

data class Point(var x: Int, var y: Int) {
    companion object {
        operator fun invoke(string: String): Point {
            val parts = string.split(",")
            return Point(parts[0].toInt(), parts[1].toInt())
        }
    }
}