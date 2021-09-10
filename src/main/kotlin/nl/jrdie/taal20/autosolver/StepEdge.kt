package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField.SOFTWARE_OPDRACHT
import nl.jrdie.taal20.data.KostenkaartField.VERBRUIK_STAP_VOORUIT
import nl.jrdie.taal20.data.getField
import nl.jrdie.taal20.glade.Glade
import nl.jrdie.taal20.glade.models.Point

class StepEdge(from: Point, to: Point, glade: Glade) : AbstractProgrammaEdge(from, to, glade) {

    override fun generateCode(): String {
        return "stapVooruit\n".repeat(Math.max(0, from.distance(to)))
    }

    override fun getRunPrice(kostenkaart: Kostenkaart): Int {
        return singlePrice(kostenkaart) * from.distance(to)
    }

    private fun singlePrice(kostenkaart: Kostenkaart): Int {
        val dis: Int = from.distance(to)
        return kostenkaart.getField(SOFTWARE_OPDRACHT) + kostenkaart.getField(VERBRUIK_STAP_VOORUIT) * dis
    }

}
