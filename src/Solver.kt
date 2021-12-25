import java.nio.file.Paths
import kotlin.reflect.full.createInstance

fun solveDay(day: Int): Double {
    println ("Solving day $day...")
    val dayClass = Class.forName("days.day$day.Day$day")
    val instance = dayClass.kotlin.createInstance()
    val start = System.currentTimeMillis()
    dayClass.methods.first { it.name == "solve" }.invoke(instance, Paths.get("src/resources/day${day}.txt"))
    println("")
    val executionTime = (System.currentTimeMillis() - start)/1000.0
    println("Execution time: ${executionTime}s")
    return executionTime
}