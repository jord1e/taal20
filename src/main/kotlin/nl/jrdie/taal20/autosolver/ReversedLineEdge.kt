package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.data.getField

class ReversedLineEdge(private val reversable: AbstractProgrammaEdge) : AbstractProgrammaEdge(reversable.from, reversable.to, reversable.glade) {

    override fun getRunPrice(kostenkaart: Kostenkaart): Int {
        val dis: Int = from.distance(to)

        if (reversable is StepEdge) {
            return kostenkaart.getField(KostenkaartField.SOFTWARE_OPDRACHT) +
                    kostenkaart.getField(KostenkaartField.VERBRUIK_STAP_ACHTERUIT) * dis
        }

        if (reversable is ZolangEdge) {
            return reversable.initPrice(kostenkaart) +
                    kostenkaart.getField(KostenkaartField.VERBRUIK_TOEWIJZING_A_IS_1) * dis +
                    kostenkaart.getField(KostenkaartField.VERBRUIK_STAP_ACHTERUIT) * dis +
                    kostenkaart.getField(KostenkaartField.VERBRUIK_VERGELIJKING) * (dis + 1) +
                    kostenkaart.getField(KostenkaartField.VERBRUIK_OPERATIE) * dis
        }

        return reversable.getPrice(kostenkaart)
    }

    override fun generateCode(): String {
        return reversable.generateCode().replace("Vooruit", "Achteruit")
    }

}
