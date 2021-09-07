package nl.jrdie.taal20.glade

data class ObstakelTile(override val value: Int) : Tile {

    init {
        check(value in 0..3)
    }

    override val tileColor: TileColor?
        get() = null

    override fun toString(): String {
        return "O$value"
    }

}
