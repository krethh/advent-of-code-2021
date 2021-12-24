import java.nio.file.Paths
import kotlin.reflect.full.createInstance

fun solveDay(day: Int) {
    println ("Solving day $day...")
    val dayClass = Class.forName("days.day$day.Day$day")
    val instance = dayClass.kotlin.createInstance()
    val start = System.currentTimeMillis()
    dayClass.methods.first { it.name == "solve" }.invoke(instance, Paths.get("src/resources/day${day}.txt"))
    println("")
    println("Execution time: ${(System.currentTimeMillis() - start)/1000.0}s")
}