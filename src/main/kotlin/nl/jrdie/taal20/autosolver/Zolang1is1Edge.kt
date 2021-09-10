package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.data.getField
import nl.jrdie.taal20.glade.Glade
import nl.jrdie.taal20.glade.models.Point

class Zolang1is1Edge(from: Point, to: Point, glade: Glade) : AbstractProgrammaEdge(from, to, glade) {

    override fun generateCode(): String {
        return "zolang 1 == 1 {\nstapVooruit\n}"
    }

    override fun getRunPrice(kostenkaart: Kostenkaart): Int {
        val dis: Int = from.distance(to)
        return initPrice(kostenkaart) +
                kostenkaart.getField(KostenkaartField.VERBRUIK_STAP_VOORUIT) * dis +
                kostenkaart.getField(KostenkaartField.VERBRUIK_VERGELIJKING) * (dis + 1)
    }

    private fun initPrice(priceTable: Kostenkaart): Int {
        return priceTable.getField(KostenkaartField.SOFTWARE_ZOLANG_LUS) +
                priceTable.getField(KostenkaartField.SOFTWARE_OPDRACHT)
    }

}
