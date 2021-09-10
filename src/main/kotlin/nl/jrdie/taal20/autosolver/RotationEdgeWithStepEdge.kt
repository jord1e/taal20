package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField.*
import nl.jrdie.taal20.data.getField
import nl.jrdie.taal20.glade.models.Direction
import kotlin.math.abs

class RotationEdgeWithStepEdge(from: Direction, to: Direction, private var turn: Int) : RotationEdge(from, to) {

    companion object {
        const val WRONG = 10000
        var random = false
    }

    override fun generateCode(): String {
        when (turn) {
            0 -> {
                RotationEdgeWithStepEdge.random = true
                return "zolang kompas != ${to.ordinal} {\ndraaiLinks\n}\n"
            }
            1 -> {
                val diff = from.stepsTo(to)
                if (diff == 0) return "\n"
                if (abs(diff) == 2) return "draaiRechts\n"
                if (diff == -3 || diff == 1) return "stapAchteruit\nstapVooruit\ndraaiRechts\n"
                if (diff == 3 || diff == -1) {
                    return "stapAchteruit\nstapVooruit\n"
                } else {
                    return "\n"
                }
            }
            2 -> {
                val diff = from.stepsTo(to)
                if (diff == 0) return "\n"
                if (abs(diff) == 2) return "draaiLinks\ndraaiLinks\n"
                if (diff == -3 || diff == 1) return "draaiRechts\n"
                if (diff == 3 || diff == -1) {
                    return "draaiLinks\n"
                } else {
                    return "\n"
                }
            }
            3 -> {
                val diff = from.stepsTo(to)
                if (diff == 0) return "\n"
                if (abs(diff) == 2) return "draaiLinks\n"
                if (diff == -3 || diff == 1) return "stapAchteruit\nstapVooruit\n"
                if (diff == 3 || diff == -1) {
                    return "stapAchteruit\nstapVooruit\ndraaiLinks\n"
                } else {
                    return "\n"
                }
            }
        }
        return ""
    }

    override fun getPrice(kostenkaart: Kostenkaart): Int {
        when (turn) {
            0 -> return kostenkaart.getField(SOFTWARE_ZOLANG_LUS) +
                    kostenkaart.getField(SOFTWARE_OPDRACHT) +
                    kostenkaart.getField(VERBRUIK_DRAAI_LINKS) * 4 +
                    kostenkaart.getField(VERBRUIK_KOMPAS) * 4 +
                    kostenkaart.getField(VERBRUIK_VERGELIJKING) * 5
            1 -> {
                val diff = from.stepsTo(to)
                if (diff == 0) return 0
                if (abs(diff) == 2) return getSingle(kostenkaart)
                if (diff == -3 || diff == 1) {
                    return getBackward(kostenkaart) + getForward(kostenkaart) + getSingle(kostenkaart) + WRONG
                }
                if (diff == 3 || diff == -1) {
                    return getBackward(kostenkaart) + getForward(kostenkaart) + WRONG
                } else {
                    return 0
                }
            }
            2 -> {
                val diff = from.stepsTo(to)
                if (diff == 0) return 0
                if (abs(diff) == 2) return getSingle(kostenkaart) * 2
                if (diff == -3 || diff == 1) return getSingle(kostenkaart)
                if (diff == 3 || diff == -1) {
                    return getSingle(kostenkaart)
                } else {
                    return 0
                }
            }
            3 -> {
                val diff = from.stepsTo(to)
                if (diff == 0) return 0
                if (abs(diff) == 2) return getSingle(kostenkaart)
                if (diff == -3 || diff == 1) return getBackward(kostenkaart) + getForward(kostenkaart) + WRONG
                if (diff == 3 || diff == -1) {
                    return getBackward(kostenkaart) + getForward(kostenkaart) + getSingle(
                        kostenkaart
                    ) + WRONG
                } else {
                    return 0
                }
            }
        }
        return 0
    }

    private fun getSingle(priceTable: Kostenkaart): Int {
        return priceTable.getField(SOFTWARE_OPDRACHT) + priceTable.getField(VERBRUIK_DRAAI_RECHTS)
    }

    private fun getForward(priceTable: Kostenkaart): Int {
        return priceTable.getField(SOFTWARE_OPDRACHT) + priceTable.getField(VERBRUIK_STAP_VOORUIT)
    }

    private fun getBackward(priceTable: Kostenkaart): Int {
        return priceTable.getField(SOFTWARE_OPDRACHT) + priceTable.getField(VERBRUIK_STAP_ACHTERUIT)
    }

}