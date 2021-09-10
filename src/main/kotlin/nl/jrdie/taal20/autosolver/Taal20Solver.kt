package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.glade.*
import nl.jrdie.taal20.glade.interpreter.Robot
import nl.jrdie.taal20.glade.models.Direction
import nl.jrdie.taal20.glade.models.Point
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DirectedWeightedMultigraph
import org.jgrapht.nio.Attribute
import org.jgrapht.nio.DefaultAttribute.createAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.io.StringWriter
import java.util.Random

object Taal20Solver {

    @OptIn(ExperimentalStdlibApi::class)
    fun dumpGraph(graph: Graph<Robot, ProgrammaEdge>): String {
        val exporter =
            DOTExporter<Robot, ProgrammaEdge> { "\"(${it.location.x}, ${it.location.y}) " + it.direction.name + "\"" }
        exporter.setVertexAttributeProvider {
            return@setVertexAttributeProvider buildMap<String, Attribute> {
                put("label", createAttribute(it.toString()))
            }
        }
        val writer = StringWriter()
        exporter.exportGraph(graph, writer)
        return writer.toString()
    }

    fun createGraph(glade: Glade, kostenkaart: Kostenkaart): Graph<Robot, ProgrammaEdge> {
        val graph: Graph<Robot, ProgrammaEdge> = DirectedWeightedMultigraph(ProgrammaEdge::class.java)
        println(
            "Highest goal: [" + glade.findFirstTileLoc(glade.getHighestGoal()).x + "][" + glade.findFirstTileLoc(
                glade.getHighestGoal()
            ).y + "]"
        )
//        println(GladeLoader.printGlade(glade))

        val positions: MutableList<Point> = mutableListOf()
        val grievers: MutableList<Robot> = mutableListOf()

        for (y in 0..19) {
            for (x in 0..19) {
                val tile = glade.tileAtRv(Point(x, y))
                val pos = Point(x, y)
                if (tile is BomTile && tile.value == 0) {
                    continue
                }
                if (tile !is ObstakelTile) {
                    positions.add(pos)
                    for (direction in Direction.allDirections()) {
                        val griever = Robot(pos, direction)
                        graph.addVertex(griever)
                        grievers.add(griever)
                    }
                }
            }
        }
        println("A: edges=${graph.edgeSet().size}, vertices=${graph.vertexSet().size}")
        println(
            "Grievers (${grievers.size}): ${
                grievers.map { "[[x=${it.location.x}, y=${it.location.y}], ${it.direction.ordinal}]" }.sorted()
            }"
        )
        for (griever in grievers) {
            val (posX, posY) = griever.location
            when (griever.direction) {
                Direction.ZUID -> {
                    for (y in (posY + 1) until 20)
                        if (addEdge(graph, griever, griever.copy(location = Point(posX, y)), glade, kostenkaart)) {
                            break
                        }
                }
                Direction.OOST -> {
                    for (x in (posX + 1) until 20) {
                        if (addEdge(graph, griever, griever.copy(location = Point(x, posY)), glade, kostenkaart)) {
                            break
                        }
                    }
                }
                Direction.NOORD -> {
                    for (y in (posY - 1) downTo 1) {
                        if (addEdge(graph, griever, griever.copy(location = Point(posX, y)), glade, kostenkaart)) {
                            break
                        }
                    }
                }
                Direction.WEST -> {
                    for (x in (posX - 1) downTo 1) {
                        if (addEdge(graph, griever, griever.copy(location = Point(x, posY)), glade, kostenkaart)) {
                            break
                        }
                    }
                }
            }
        }
        println("B: edges=${graph.edgeSet().size}, vertices=${graph.vertexSet().size}")

        for (position in positions) {
            val tile = glade.tileAtRv(position)
            if (tile is BomTile && tile.value == 1) {
                continue
            }
            for (r1 in Direction.allDirections()) {
                var r2 = r1.right()
                addDraaiEdge(graph, Robot(position, r1), Robot(position, r2), glade, kostenkaart)
                r2 = r1.right(3)
                addDraaiEdge(graph, Robot(position, r1), Robot(position, r2), glade, kostenkaart)
            }
            //            for (r1 in 0..3) {
//                var r2 = (r1 + 1) % 4
//                addDraaiEdge(
//                    graph,
//                    Robot(position, Direction.ofOrdinal(r1)),
//                    Robot(position, Direction.ofOrdinal(r2)),
//                    glade,
//                    kostenkaart
//                )
//                r2 = (r1 + 3) % 4
//                addDraaiEdge(
//                    graph,
//                    Robot(position, Direction.ofOrdinal(r1)),
//                    Robot(position, Direction.ofOrdinal(r2)),
//                    glade,
//                    kostenkaart
//                )
//            }
        }
        println("C: edges=${graph.edgeSet().size}, vertices=${graph.vertexSet().size}")

        return graph
    }

    private fun addDraaiEdge(
        graph: Graph<Robot, ProgrammaEdge>,
        p1: Robot,
        p2: Robot,
        glade: Glade,
        kostenkaart: Kostenkaart
    ) {
        val r1 = p1.direction
        var r2 = p2.direction
        if (r1 == r2) {
            return
        }

        val tile: Tile = glade.tileAtRv(p1.location)
        if (tile is DraaiTile && tile.value == 0) {
            val codeConnection: RotationEdge = RotationEdge.getBestRotationWithTurn(r1, r2, tile.value, kostenkaart)
            graph.addEdge(p1, p2, codeConnection)
            graph.setEdgeWeight(codeConnection, codeConnection.getPrice(kostenkaart).toDouble())
            return
        }

        if (tile is DraaiTile) {
            val extraRot: Int = tile.value
            r2 = r2.right(extraRot)
            if (r1 == r2) {
                return
            }
        }

        val programmaEdge: ProgrammaEdge = RotationEdge.getBestRotation(r1, r2, kostenkaart)
        graph.addEdge(p1.copy(directionOrdinal = r1.ordinal), p2.copy(directionOrdinal = r2.ordinal), programmaEdge)
        graph.setEdgeWeight(programmaEdge, programmaEdge.getPrice(kostenkaart).toDouble())
    }

    private val rnd = Random()

    private fun addEdge(
        graph: Graph<Robot, ProgrammaEdge>,
        p1: Robot,
        p2: Robot,
        glade: Glade,
        kostenkaart: Kostenkaart
    ): Boolean {
        var p2: Robot = p2
        val tileAtRP2 = glade.tileAtRv(p2.location)
        if (tileAtRP2 is DraaiTile) {
            if (tileAtRP2.value == 0) {
                val r = rnd.nextInt().coerceAtLeast(5)
                p2 = p2.copy(directionOrdinal = r)
                graph.addVertex(p2)
                val codeConnection: AbstractProgrammaEdge =
                    AbstractProgrammaEdge.getBestLine(p1.location, p2.location, glade, kostenkaart)
                graph.addEdge(p1, p2, codeConnection)
                graph.setEdgeWeight(codeConnection, codeConnection.getPrice(kostenkaart).toDouble())
                for (i in 0..3) {
                    val rot: ProgrammaEdge =
                        RotationEdge.getBestRotationWithTurn(
                            Direction.ofOrdinal(r),
                            Direction.ofOrdinal(i),
                            0,
                            kostenkaart
                        )
                    graph.addEdge(p2, p2.copy(directionOrdinal = i), rot)
                    graph.setEdgeWeight(rot, rot.getPrice(kostenkaart).toDouble())
                }
            } else {
                val codeConnection: AbstractProgrammaEdge = AbstractProgrammaEdge.getBestLine(
                    p1.location,
                    p2.location,
                    glade,
                    kostenkaart
                )
                graph.addEdge(
                    p1,
                    p2.copy(directionOrdinal = p2.direction.right(tileAtRP2.value).ordinal),
                    codeConnection
                )
                graph.setEdgeWeight(codeConnection, codeConnection.getPrice(kostenkaart).toDouble())
                addReversedEdge(graph, p1, p2, glade, kostenkaart)
            }
            return true
        }

        // Geen DraaiTile
        if (graph.containsVertex(p2)) {
            val codeConnection: AbstractProgrammaEdge = AbstractProgrammaEdge.getBestLine(
                p1.location,
                p2.location,
                glade,
                kostenkaart
            )
            graph.addEdge(p1, p2, codeConnection)
            graph.setEdgeWeight(codeConnection, codeConnection.getPrice(kostenkaart).toDouble())
            addReversedEdge(graph, p1, p2, glade, kostenkaart)
            return false
        }

        return true
    }

    private fun addReversedEdge(
        graph: Graph<Robot, ProgrammaEdge>,
        p1: Robot,
        p2: Robot,
        glade: Glade,
        priceTable: Kostenkaart
    ) {
        val tileAtRP1 = glade.tileAtRv(p1.location)
        if (tileAtRP1 is DraaiTile) {
            if (tileAtRP1.value != 0) {
                val codeConnection: AbstractProgrammaEdge = AbstractProgrammaEdge.getBestLine(
                    p1.location,
                    p2.location,
                    glade,
                    priceTable
                )
                val reverse: AbstractProgrammaEdge = ReversedLineEdge(codeConnection)
                graph.addEdge(p2, p1.copy(directionOrdinal = p2.direction.right(tileAtRP1.value).ordinal), reverse)
                graph.setEdgeWeight(reverse, reverse.getPrice(priceTable).toDouble())
            }
        } else if (graph.containsVertex(p2)) {
            val codeConnection: AbstractProgrammaEdge = AbstractProgrammaEdge.getBestLine(
                p1.location,
                p2.location,
                glade,
                priceTable
            )
            val reverse: AbstractProgrammaEdge = ReversedLineEdge(codeConnection)
            graph.addEdge(p2, p1, reverse)
            graph.setEdgeWeight(reverse, reverse.getPrice(priceTable).toDouble())
        }
    }

    private fun getShortestRoute(graph: Graph<Robot, ProgrammaEdge>, glade: Glade): List<Robot> {
        println("ZZ: edges=${graph.edgeSet().size}, vertices=${graph.vertexSet().size}")
        return graph.vertexSet()
            .map(SimpleMap.hold { p: Robot -> glade.tileAtRv(Point(p.location.x, p.location.y)) })
            .filter { (_, tile) ->
                tile is StartTile || tile is DoelTile
            }
            .flatMap { holder ->
                if (holder.value is DoelTile || (holder.value.value == holder.key.direction.ordinal)) {
                    listOf(holder)
                } else {
                    emptyList()
                }
            }
            .sortedWith(comparator)
            .map { it.key }
    }

    private val comparator: Comparator<SimpleMap<Robot, Tile>> = Comparator { o1, o2 ->
        if (o1.value is StartTile) {
            return@Comparator -1
        }
        if (o2.value is StartTile) {
            return@Comparator 1
        }
        return@Comparator o1.value.value - o2.value.value
    }

    fun generate(glade: Glade, kostenkaart: Kostenkaart): String {
        val graph: Graph<Robot, ProgrammaEdge> = createGraph(glade, kostenkaart)
        println("===")
        println(dumpGraph(graph))
        println("===")
        val route: MutableList<Robot> = getShortestRoute(graph, glade).toMutableList()

        @Suppress("UNCHECKED_CAST")
        val routes: Graph<Robot, List<ProgrammaEdge>> =
            DirectedWeightedMultigraph<Robot, List<*>>(List::class.java)
                    as DirectedWeightedMultigraph<Robot, List<ProgrammaEdge>>

        route.forEach { v: Robot -> routes.addVertex(v) }
        val start: Robot = route.removeAt(0)

        println("R: routes=${route.size}, ${route}")
        addEdges(graph, routes, start, route.subList(0, 4), kostenkaart)

        for (i in 0 until route.size - 4) {
            val s: Robot = route[i]
            addEdges(graph, routes, s, route.subList(i / 4 * 4 + 4, i / 4 * 4 + 8), kostenkaart)
        }

        val paths: SingleSourcePaths<Robot, List<ProgrammaEdge>> = DijkstraShortestPath(routes).getPaths(start)
        var min = -1
        var best: List<ProgrammaEdge>? = null

        for (i in 1..4) {
            val finish: Robot = route[route.size - i]
            val ourRoute = paths.getPath(finish).edgeList.flatten()
            val price = ourRoute.sumOf { it.getPrice(kostenkaart) }
            if (min == -1 || price < min) {
                min = price
                best = ourRoute
            }
        }

        println(best)
        var pretend = ""
        val completeCode = StringBuilder()
        for (codeConnection in best!!) {
            completeCode.append(codeConnection.generateCode()).append("\n")
        }

        if (ZolangEdge.offset != 0) {
            pretend += "gebruik a\n"
        }

        if (RotationEdgeWithStepEdge.random) {
            pretend += "gebruik kompas\n"
        }
        return pretend + completeCode.toString()
    }

    private fun addEdges(
        graph: Graph<Robot, ProgrammaEdge>,
        route: Graph<Robot, List<ProgrammaEdge>>,
        start: Robot,
        path: List<Robot>,
        kostenkaart: Kostenkaart
    ) {
        val paths: SingleSourcePaths<Robot, ProgrammaEdge> = DijkstraShortestPath(graph).getPaths(start)
        for (griever in path) {
            val graphPath: GraphPath<Robot, ProgrammaEdge> = paths.getPath(griever)
            val r = graphPath.edgeList
            val price = r.sumOf { it.getPrice(kostenkaart) }
            route.addEdge(start, griever, r)
            route.setEdgeWeight(r, price.toDouble())
        }
    }

}
