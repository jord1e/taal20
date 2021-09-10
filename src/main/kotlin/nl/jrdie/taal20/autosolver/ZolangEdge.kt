package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.data.getField
import nl.jrdie.taal20.glade.Glade
import nl.jrdie.taal20.glade.models.Point

class ZolangEdge(from: Point, to: Point, glade: Glade) : AbstractProgrammaEdge(from, to, glade) {

    override fun generateCode(): String {
        val dis: Int = from.distance(to)
        val code = "zolang a < ${dis + offset} {\na = a + 1\nstapVooruit\n}"
        offset += dis
        return code
    }

    override fun getRunPrice(kostenkaart: Kostenkaart): Int {
        val dis: Int = from.distance(to)
        return initPrice(kostenkaart) +
                kostenkaart.getField(KostenkaartField.VERBRUIK_TOEWIJZING_A_IS_1) * dis +
                kostenkaart.getField(KostenkaartField.VERBRUIK_STAP_VOORUIT) * dis +
                kostenkaart.getField(KostenkaartField.VERBRUIK_VERGELIJKING) * (dis + 1) +
                kostenkaart.getField(KostenkaartField.VERBRUIK_OPERATIE) * dis
    }

    fun initPrice(priceTable: Kostenkaart): Int {
        return priceTable.getField(KostenkaartField.SOFTWARE_ZOLANG_LUS) +
                priceTable.getField(KostenkaartField.SOFTWARE_TOEKENNING) +
                priceTable.getField(KostenkaartField.SOFTWARE_OPDRACHT)
    }

    companion object {
        var offset = 0
            private set
    }

    init {
        offset = 0
    }

}
