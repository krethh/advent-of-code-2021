package days.day17

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

class Day17 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val targetArea = TargetArea(209, 238, -86, -59)

        var succesfulThrows = 0
        var maxHeight = 0
        for (x in IntRange(0, targetArea.xMax)) {
            for (y in IntRange(targetArea.yMin, 500)) {
                var maxHeightThisThrow = 0
                val startingVelocity = Velocity(x, y)
                var state = State(startingVelocity = startingVelocity, velocity = startingVelocity)

                while (true) {
                    state = state.move()
                    maxHeightThisThrow = max(maxHeightThisThrow, state.position.y)
                    if (state.isWithin(targetArea)) {
                        maxHeight = max(maxHeight, maxHeightThisThrow)
                        succesfulThrows++
                        break
                    }
                    if (state.wontReach(targetArea)) {
                        break
                    }
                }
            }
        }

        println(maxHeight)
        println(succesfulThrows)
    }

    private fun State.wontReach(targetArea: TargetArea): Boolean {
        return this.position.y <= targetArea.yMin || this.position.x >= targetArea.xMax
    }

    private fun State.isWithin(targetArea: TargetArea): Boolean {
        return this.position.x >= targetArea.xMin
                && this.position.x <= targetArea.xMax
                && this.position.y >= targetArea.yMin
                && this.position.y <= targetArea.yMax
    }

    private fun State.move(): State {
        val newX = this.position.x + this.velocity.x
        val newY = this.position.y + this.velocity.y
        val newVelocityX = abs(this.velocity.x - 1) * this.velocity.x.sign
        val newVelocityY = this.velocity.y - 1

        return State(Position(newX, newY), this.startingVelocity, Velocity(newVelocityX, newVelocityY))
    }

    data class TargetArea(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)
    data class Position(val x: Int, val y: Int)
    data class Velocity(val x: Int, val y: Int)
    data class State(val position: Position = Position(0, 0), val startingVelocity: Velocity, val velocity: Velocity)
}