package days.day1

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Day1 {
    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toInt() }

        var counter = 0;
        for (i in 1 until items.size) {
            if (items[i] > items[i - 1]) {
                counter ++;
            }
        }

        println(counter)

        counter = 0;
        var lastWindow = items[0] + items[1] + items[2];
        for (i in 3 until items.size) {
            val thisWindow = items[i] + items[i - 1] + items[i - 2]
            if (thisWindow > lastWindow) {
                counter++;
            }
            lastWindow = thisWindow;
        }

        println(counter)
    }
}