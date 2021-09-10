package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.data.getField
import nl.jrdie.taal20.glade.models.Direction
import kotlin.math.abs

class SimpleDraaiEdge(from: Direction, to: Direction) : RotationEdge(from, to) {

    override fun generateCode(): String {
        val diff = from.stepsTo(to)
        if (diff == 0) return "\n"

        if (abs(diff) == 2) {
            return "draaiRechts\ndraaiRechts\n"
        }

        if (diff == -3 || diff == 1) {
            return "draaiLinks\n"
        }

        if (diff == 3 || diff == -1) {
            return "draaiRechts\n"
        } else {
            return "\n"
        }
    }

    override fun getPrice(kostenkaart: Kostenkaart): Int {
        val diff = from.stepsTo(to)
        if (diff == 0) {
            return 0
        }

        if (abs(diff) == 2) {
            return getSingle(kostenkaart) * 2
        } else {
            return getSingle(kostenkaart)
        }
    }

    private fun getSingle(priceTable: Kostenkaart): Int {
        return priceTable.getField(KostenkaartField.SOFTWARE_OPDRACHT) + priceTable.getField(KostenkaartField.VERBRUIK_DRAAI_RECHTS)
    }

}