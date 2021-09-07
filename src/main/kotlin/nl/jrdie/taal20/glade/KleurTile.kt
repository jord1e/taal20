package nl.jrdie.taal20.glade

data class KleurTile(override val value: Int) : Tile {

    init {
        check(value in 0..8)
    }

    override val tileColor: TileColor
        get() = when (value) {
            8 -> TileColor.WIT
            7 -> TileColor.GRIJS
            6 -> TileColor.ROOD
            5 -> TileColor.ORANJE
            4 -> TileColor.GEEL
            3 -> TileColor.GROEN
            2 -> TileColor.BLAUW
            1 -> TileColor.PAARS
            0 -> TileColor.ZWART
            else -> throw RuntimeException("Unknown value: $value")
        }

    override fun toString(): String {
        return "C$value"
    }

}
