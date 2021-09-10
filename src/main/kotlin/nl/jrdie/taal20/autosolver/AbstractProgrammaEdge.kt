package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.glade.BonusTile
import nl.jrdie.taal20.glade.DoelTile
import nl.jrdie.taal20.glade.Glade
import nl.jrdie.taal20.glade.Tile
import nl.jrdie.taal20.glade.models.Point

abstract class AbstractProgrammaEdge(var from: Point, var to: Point, var glade: Glade) : ProgrammaEdge {

    abstract fun getRunPrice(kostenkaart: Kostenkaart): Int

    fun getEarnings(): Int {
        var earning = 0

        val min: Point = Point.min(from, to)
        val max: Point = Point.max(from, to)

        for (x in min.x until max.x) {
            val tile: Tile = glade.tileAtRv(Point(x, min.y))
            if (tile is BonusTile) {
                earning += tile.getBonus()
            }
        }
        for (y in min.y until max.y) {
            val tile: Tile = glade.tileAtRv(Point(min.x, y))
            if (tile is BonusTile) {
                earning += tile.getBonus()
            }
        }

        return 0
    }

    override fun getPrice(kostenkaart: Kostenkaart): Int {
        return getRunPrice(kostenkaart) - getEarnings()
    }

    override fun toString(): String {
        return "[$from -> $to]"
    }

    companion object {
        fun getBestLine(from: Point, to: Point, glade: Glade, kostenkaart: Kostenkaart): AbstractProgrammaEdge {
            val toTile = glade.tileAtRv(to)

            // Als de tegel waar we heen moeten bewegen het einddoel is kunnen we gewoon vooruit lopen
            val finalConnection: Zolang1is1Edge?
            if (toTile is DoelTile && toTile == glade.getHighestGoal()) {
                finalConnection = Zolang1is1Edge(from, to, glade)
            } else {
                finalConnection = null
            }

            val possibleEdges: List<AbstractProgrammaEdge?> =
                listOf(StepEdge(from, to, glade), ZolangEdge(from, to, glade), finalConnection)

            return possibleEdges
                .filterNotNull()
                .minByOrNull { it.getRunPrice(kostenkaart) }!!
        }
    }

}