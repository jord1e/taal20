package nl.jrdie.taal20.glade

data class DoelTile(override val value: Int) : Tile {

    init {
        check(value in 1..9)
    }

    override val tileColor: TileColor
        get() = TileColor.GEEL

    override fun toString(): String {
        return "D$value"
    }

}
