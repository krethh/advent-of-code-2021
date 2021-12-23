package days.day23

import java.io.IOException
import java.nio.file.Path
import java.util.*

object Day23 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val nodes = listOf(
            Node("r10", "C"),
            Node("r11", "B"),
            Node("r20", "A"),
            Node("r21", "B"),
            Node("r30", "D"),
            Node("r31", "D"),
            Node("r40", "C"),
            Node("r41", "A"),
            Node("h1", "."),
            Node("h2", "."),
            Node("h3", "."),
            Node("h4", "."),
            Node("h5", "."),
            Node("h6", "."),
            Node("h7", ".")
        )

        val edges = listOf(
            Edge("r10" , "r11", 1),
            Edge("r20" , "r21", 1),
            Edge("r30" , "r31", 1),
            Edge("r40" , "r41", 1),
            Edge("h1", "h2", 1),
            Edge("h2", "h3", 2),
            Edge("h3", "h4", 2),
            Edge("h4", "h5", 2),
            Edge("h5", "h6", 2),
            Edge("h6", "h7", 1),
            Edge("r11", "h2", 2),
            Edge("r11", "h3", 2),
            Edge("r21", "h3", 2),
            Edge("r21", "h4", 2),
            Edge("r31", "h4", 2),
            Edge("r31", "h5", 2),
            Edge("r41", "h5", 2),
            Edge("r41", "h6", 2),
        )

        val graph = Graph(nodes, edges)
        val state = State(graph, 0)
        val comparator = compareBy<State> { it.energy }
        val queue = PriorityQueue(comparator)
        queue.add(state)
        val visited = mutableMapOf<String, Int>()
        val results = mutableSetOf<Int>()

        while (queue.isNotEmpty()) {
            val current = queue.remove()

            if (current.graph.isTarget()) {
                results.add(current.energy)
                break
            }

            val nodesWithAmphipods = current.graph.nodes.filter { it.hasAmphipod() }
            nodesWithAmphipods.forEach {
                val nextMoves = current.getPossibleNextMoves(it, visited, !it.isHallway())
                queue.addAll(nextMoves)
            }
        }

        println(results.minOrNull())
    }

    fun String.getEnergyMultiplier() = when (this) {
        "A" -> 1
        "B" -> 10
        "C" -> 100
        else -> 1000
    }

    private fun Node.isHallway() = this.id.startsWith("h")

    fun Graph.copyGraph(): Graph  {
        val nodes = this.nodes.map { it.copy() }
        val edges = this.edges.map { it.copy() }
        return Graph(nodes, edges)
    }

    private fun Graph.getNeighbors(node: Node) =
        edges.filter { it.first == node.id }.map { e -> nodes.first { e.second == it.id } }
            .plus(edges.filter { it.second == node.id }.map { e -> nodes.first { e.first == it.id } })

    private fun Graph.costBetween(first: Node, second: Node) = this.edges.firstOrNull {
        (it.first == first.id && it.second == second.id)
                || (it.first == second.id && it.second == first.id)
    }?.cost ?: Int.MAX_VALUE

    private fun Graph.canMoveIntoARoom(start: Node, room: Node): Boolean {
        val roomPrefix = room.id.substring(0..1)
        val allRoomNodes = this.nodes.filter { it.id.startsWith(roomPrefix) }
        val currentOccupantsOfRoom = allRoomNodes.filter { it.hasAmphipod() }.map { it.occupant }
        val triesToEscapeRoom = currentOccupantsOfRoom.size == 1 && currentOccupantsOfRoom[0] == start.occupant

        if (currentOccupantsOfRoom.any { it != roomPrefix.properRoomOccupant() } && !triesToEscapeRoom) {
            return false
        }

        if (start.occupant != roomPrefix.properRoomOccupant() && !triesToEscapeRoom) {
            return false
        }

        if (room.id !in allRoomNodes.map { it.id }) {
            return false
        }

        return true
    }

    fun Graph.isLegalNextMove(start: Node, end: Node): Boolean {
        if (end.hasAmphipod()) {
            return false
        }

        if (!end.isHallway() && !this.canMoveIntoARoom(start, end)) {
            return false
        }

        if (this.alreadyInGoodRoom(start)) {
            return false
        }

        return true;
    }


    private fun Graph.alreadyInGoodRoom(node: Node): Boolean {
        if (!node.id.startsWith("r")) {
            return false
        }

        val roomPrefix = node.id.substring(0..1)
        val thisRoomOccupants = this.nodes.filter { it.id.startsWith(roomPrefix) }.map { it.occupant }
        val properRoomOccupant = roomPrefix.properRoomOccupant()

        return thisRoomOccupants.all { it == properRoomOccupant }
    }

    private fun String.properRoomOccupant() = when (this) {
        "r1" -> "A"
        "r2" -> "B"
        "r3" -> "C"
        else -> "D"
    }

    fun State.getPossibleNextMoves(start: Node, visited: MutableMap<String, Int>, canEndInHallway: Boolean): List<State> {
        if (!start.hasAmphipod()) {
            return listOf()
        }

        val neighbors = this.graph.getNeighbors(start)

        // implement: if starting from a room get all possible hallway or room moves
        // if starting from a hallway, get possible room endpoints (there aren't many)
        // you cannot generate only the closest step. all possible end positions must be generated at once
        // implement a visited hash set
        val nextStates = mutableListOf<State>()

        neighbors.forEach { neighbor ->
            val legal = this.graph.isLegalNextMove(start, neighbor)

            if (legal) {
                val newGraph = this.graph.copyGraph()
                val currentOccupant = start.occupant
                newGraph.nodes.first { it.id == start.id }.occupant = "."
                newGraph.nodes.first { it.id == neighbor.id }.occupant = currentOccupant
                val serialized = newGraph.serialized()
                val newCost = this.graph.costBetween(start, neighbor) * currentOccupant.getEnergyMultiplier() + this.energy
                val newState = State(newGraph, newCost)
                if (serialized !in visited.keys || (visited[serialized] ?: Int.MAX_VALUE) > newCost) {
                    visited[serialized] = newCost
                    if (!(neighbor.isHallway() && !canEndInHallway)) {
                        nextStates.add(newState)
                    }
                    nextStates.addAll(newState.getPossibleNextMoves(newGraph.nodes.first { it.id == neighbor.id }, visited, canEndInHallway))
                }
            }

        }

        return nextStates
    }

    private fun Node.hasAmphipod() = this.occupant in listOf("A", "B", "C", "D")

    private fun Graph.isTarget(): Boolean {
        return nodes.first { it.id == "r10" }.occupant == "A" &&
                nodes.first { it.id == "r11" }.occupant == "A" &&
                nodes.first { it.id == "r20" }.occupant == "B" &&
                nodes.first { it.id == "r21" }.occupant == "B" &&
                nodes.first { it.id == "r30" }.occupant == "C" &&
                nodes.first { it.id == "r31" }.occupant == "C" &&
                nodes.first { it.id == "r40" }.occupant == "D" &&
                nodes.first { it.id == "r41" }.occupant == "D"
    }

    private fun Graph.serialized() = nodes.sortedBy { it.id }.joinToString("") { "${it.id}-${it.occupant} " }

    data class Node(val id: String, var occupant: String)
    data class Edge(val first: String, val second: String, val cost: Int)
    data class Graph(val nodes: List<Node>, val edges: List<Edge>)
    data class State(val graph: Graph, val energy: Int)

}