package nl.jrdie.taal20.glade

data class BonusTile(override val value: Int) : Tile {

    init {
        check(value in 1..9)
    }

    fun getBonus(): Int {
        return Math.pow(2.0, value.toDouble()).toInt()
    }

    override val tileColor: TileColor
        get() = TileColor.GEEL

    override fun toString(): String {
        return "E$value"
    }

}
