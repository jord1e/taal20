package nl.jrdie.taal20.glade

data class DraaiTile(override val value: Int) : Tile {

    init {
        check(value in 0..3)
    }

    override val tileColor: TileColor
        get() = TileColor.BLAUW

    override fun toString(): String {
        return "R$value"
    }

}
