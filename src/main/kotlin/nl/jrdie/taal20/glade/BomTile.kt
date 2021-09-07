package nl.jrdie.taal20.glade

data class BomTile(override val value: Int) : Tile {

    init {
        check(value in 0..8)
    }

    override val tileColor: TileColor
        get() = TileColor.ZWART

    override fun toString(): String {
        return "B$value"
    }

}
