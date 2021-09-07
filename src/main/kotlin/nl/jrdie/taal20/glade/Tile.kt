package nl.jrdie.taal20.glade

sealed interface Tile {
    val value: Int
    val tileColor: TileColor?
    abstract override fun toString(): String
}
