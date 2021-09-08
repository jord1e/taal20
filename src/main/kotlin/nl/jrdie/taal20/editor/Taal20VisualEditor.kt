package nl.jrdie.taal20.editor

import com.formdev.flatlaf.FlatLightLaf

fun main() {
    FlatLightLaf.setup()
    val frame = Taal20Frame()
    frame.isVisible = true
}
