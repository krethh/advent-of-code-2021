package days.day5

class Line(var start: Point, var end: Point) {

    fun isHorizontalOrVertical() = start.x == end.x || start.y == end.y

    fun isHorizontal() = start.y == end.y

    fun isVertical() = start.x == end.x

    fun getAllPointsCovered(): List<Point> {
        if (isHorizontal()) {
            val startPoint = if (start.x > end.x) end else start
            val endPoint = if (start.x > end.x) start else end
            return IntRange(startPoint.x, endPoint.x).map { Point(it, start.y) }
        } else if(isVertical()) {
            val startPoint = if (start.y > end.y) end else start
            val endPoint = if (start.y > end.y) start else end
            return IntRange(startPoint.y, endPoint.y).map { Point(start.x, it) }
        } else {
            // check if 45 degrees
            // slope has to be 1 or -1

            val startPoint = if (start.x > end.x) end else start
            val endPoint = if (start.x > end.x) start else end

            val slope = (startPoint.y - endPoint.y * 1.0) / (startPoint.x - endPoint.x)
            if (Math.abs(slope) != 1.0) {
                return listOf()
            }
            val points = mutableListOf<Point>()
            if (slope > 0) {
                var currentPoint = Point(startPoint.x, startPoint.y)

                do {
                    points.add(currentPoint)
                    currentPoint = Point(currentPoint.x + 1, currentPoint.y + 1)
                } while(currentPoint != endPoint)
                points.add(currentPoint)
            } else {
                var currentPoint = Point(startPoint.x, startPoint.y)
                do {
                    points.add(currentPoint)
                    currentPoint = Point(currentPoint.x + 1, currentPoint.y - 1)
                } while(currentPoint != endPoint)
                points.add(currentPoint)

            }
            return points
        }
    }

}