package nl.jrdie.taal20.editor

import nl.jrdie.taal20.glade.*
import nl.jrdie.taal20.glade.interpreter.Taal20Interpreter
import nl.jrdie.taal20.glade.models.Point
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.UncheckedIOException
import javax.imageio.ImageIO
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

class Taal20GladePanel : JPanel(GridLayout(20, 20, 1, 1)) {

    var images: MutableMap<String, BufferedImage>

    init {
        preferredSize = Dimension(660, 660)
        maximumSize = Dimension(660, 660)
        size = Dimension(660, 660)
        minimumSize = Dimension(660, 660)
        images = mutableMapOf()
        renderGrid()
    }

    fun renderGrid(glade: Glade) {
        removeAll()
        for (row in glade.matrix) {
            for (col in row) {
                add(renderTileLabel(col))
            }
        }
        repaint()
        revalidate()
    }

    fun renderGrid() {
        (1..400).forEach { _ -> add(renderTileLabel(KleurTile(8))) }
    }

    fun renderInterpreterState(interpreter: Taal20Interpreter) {
        removeAll()
        for ((x, row) in interpreter.glade.matrix.withIndex()) {
            for ((y, col) in row.withIndex()) {
                val point = Point(x, y)
                val currentTile = interpreter.glade.tileAt(point)
                var label = renderTileLabel(col)
                if (point == interpreter.position) {
                    label = JLabel(ImageIcon(loadImage("start.png")))
                    label.border = BorderFactory.createLineBorder(Color.RED, 2)
                } else if (currentTile is StartTile) {
                    label = JLabel(ImageIcon(loadImage("black.png")))
                } else if (currentTile is BonusTile) {
                    if (interpreter.bonusTilesVisited.contains(point)) {
                        label = JLabel(ImageIcon(loadImage("yellow.jpg")))
                    }
                } else if (currentTile is DoelTile) {
                    if (interpreter.doelen[point] == true) {
                        label = JLabel(ImageIcon(loadImage("yellow.jpg")))
                    }
                }

                add(label)
            }
        }
        repaint()
        revalidate()
    }

    private fun renderTileLabel(tile: Tile): JLabel {
        val image: BufferedImage = when (tile) {
            is BomTile -> loadImage("bomb.png")
            is BonusTile -> loadImage("bonus.gif")
            is DoelTile -> loadImage("goal.png")
            is DraaiTile -> loadImage("turn.png")
            is StartTile -> loadImage("start.png")
            is KleurTile -> when (tile.tileColor) {
                TileColor.WIT -> loadImage("white.png")
                TileColor.GRIJS -> loadImage("gray.jpg")
                TileColor.ROOD -> loadImage("red.jpg")
                TileColor.ORANJE -> loadImage("orange.jpg")
                TileColor.GEEL -> loadImage("yellow.jpg")
                TileColor.GROEN -> loadImage("green.jpg")
                TileColor.BLAUW -> loadImage("blue.gif")
                TileColor.PAARS -> loadImage("purple.jpg")
                TileColor.ZWART -> loadImage("black.png")
            }
            is ObstakelTile -> when (tile.value) {
                0 -> loadImage("debris.gif")
                1 -> loadImage("bush.gif")
                2 -> loadImage("stone.gif")
                3 -> loadImage("wood.png")
                else -> throw RuntimeException("Invalid ObstakelTile value: ${tile.value}")
            }
        }
        val imageIcon = ImageIcon(image)
        return when (tile) {
            is DoelTile -> JLabel("D" + tile.value)
            is DraaiTile -> JLabel("R" + tile.value)
            else -> JLabel(imageIcon)
        }
    }

    private fun loadImage(name: String): BufferedImage {
        if (images.containsKey(name)) {
            return images[name]!!
        }
        val resource = Taal20GladePanel::class.java.getResource("/$name")
        try {
            val read = ImageIO.read(resource)
            images[name] = read
            return read
        } catch (e: IOException) {
            println("Kon image niet laden: $name")
            throw UncheckedIOException(e)
        }
    }

}
