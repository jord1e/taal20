package nl.jrdie.taal20.editor

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartType
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable

class KostenkaartPanel(private val kostenkaart: Kostenkaart): JPanel(FlowLayout(FlowLayout.CENTER)) {

    init {
        val hardware = kostenkaart
            .filter { it.key.type == KostenkaartType.HARDWARE }
            .map { arrayOf(it.key.friendlyName, it.value.toString()) }
            .toTypedArray()
        add(JLabel("<html><h1>Hardware<br>(aanschaf)</h1></html>"))
        add(JTable(hardware, arrayOf("Naam", "Kosten")))

        val verbruik = kostenkaart
            .filter { it.key.type == KostenkaartType.VERBRUIK }
            .map { arrayOf(it.key.friendlyName, it.value.toString()) }
            .toTypedArray()
        add(JLabel("<html><h1>Verbruik</h1></html>"))
        add(JTable(verbruik, arrayOf("Naam", "Kosten")))

        val software = kostenkaart
            .filter { it.key.type == KostenkaartType.SOFTWARE }
            .map { arrayOf(it.key.friendlyName, it.value.toString()) }
            .toTypedArray()
        add(JLabel("<html><h1>Software<br>(per geschreven statement)</h1></html>"))
        add(JTable(software, arrayOf("Naam", "Kosten")))

        val startKapitaal = kostenkaart
            .filter { it.key.type == KostenkaartType.START_KAPITAAL }
            .map { arrayOf(it.key.friendlyName, it.value.toString()) }
            .toTypedArray()
        add(JLabel("<html><h1>Start kapitaal</h1></html>"))
        add(JTable(startKapitaal, arrayOf("Naam", "Kosten")))
    }

}
