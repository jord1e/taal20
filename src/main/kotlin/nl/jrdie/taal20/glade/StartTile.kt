package nl.jrdie.taal20.glade

import nl.jrdie.taal20.glade.models.Direction

data class StartTile(override val value: Int) : Tile {

    init {
        check(value in 0..3)
    }

    fun startDirection(): Direction {
        return when (value) {
            0 -> Direction.NOORD
            1 -> Direction.OOST
            2 -> Direction.ZUID
            3 -> Direction.WEST
            else -> throw RuntimeException("Unknown start value: $value (should not happen)")
        }
    }

    override val tileColor: TileColor
        get() = TileColor.ZWART

    override fun toString(): String {
        return "S$value"
    }

}
