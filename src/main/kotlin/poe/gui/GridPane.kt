package poe.gui

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import poe.model.Grid
import poe.model.GridArea
import poe.model.Rarity
import poe.model.Tile
import tornadofx.*

class GridSelectionEvent(val area:GridArea) : FXEvent()

object ImageCache {
    val cache = mutableMapOf<String, Image?>()
    fun getImage(filename:String) : Image? {
        return cache.computeIfAbsent(filename) {
            try {
                Image("file:poedb/images/$filename")
            } catch (e:Exception) {
                null
            }
        }
    }
}
class GridPane(grid: Grid) : Pane() {
    private var grid: Grid = grid
    val rect = Rectangle()
    val tileGroup = Group()
    init {
        minWidth = 500.0
        minHeight = 500.0

        grid.list.onChange {
            val list = ArrayList(it.list)
            Platform.runLater {
                updateTiles(list)
            }
        }

        rect.x = 0.0
        rect.y = 0.0
        rect.width = 0.0
        rect.height = 0.0
        rect.stroke = Color.BLACK
        rect.fill = Color.TRANSPARENT

        rect.isVisible = false
        var dragging = false
        onMouseDragged = EventHandler {
            if( !dragging ) {
                rect.isVisible = true
                rect.x = it.x
                rect.y = it.y
                rect.width = 0.0
                rect.height = 0.0

                dragging = true
            } else {
                rect.width = it.x - rect.x
                rect.height = it.y - rect.y
            }
        }
        onMouseReleased = EventHandler {
            rect.width = it.x - rect.x
            rect.height = it.y - rect.y
            rect.isVisible = false
            dragging = false

            val x = (grid.width*rect.x/width).toInt()
            val y = (grid.height*rect.y/height).toInt()
            val ex = (grid.width*(rect.x+rect.width)/width).toInt()
            val ey = (grid.height*(rect.y+rect.height)/height).toInt()
            FX.eventbus.fire(GridSelectionEvent(GridArea(x, y, ex-x+1, ey-y+1)))
        }
    }

    fun updateTiles(list:List<Tile>) {
        val scaleX = width / grid.width
        val scaleY = height / grid.height
        tileGroup.children.clear()
        list.forEach { tile ->
            val img = ImageCache.getImage(tile.item.filename)
            val view = ImageView(img)
            view.x = tile.x * scaleX
            view.y = tile.y * scaleY
            view.fitWidth = tile.item.width * scaleX
            view.fitHeight = tile.item.height * scaleY
            view.tooltip(tile.item.text) {
                style {
                    textFill = when (tile.item.rarity) {
                        Rarity.Normal -> Color.WHITE
                        Rarity.Magic -> Color.LIGHTBLUE
                        Rarity.Rare -> Color.YELLOW
                        Rarity.Unique -> Color.RED
                        else -> Color.WHITE
                    }
                }
            }
            tileGroup.children += view
        }

    }
    override fun layoutChildren() {
        children.clear()
        val scaleX = width / grid.width
        val scaleY = height / grid.height
        (0..grid.width).forEach { x ->
            children += Line(x * scaleX, 0.0, x * scaleX, height)
        }
        (0..grid.height).forEach { y ->
            children += Line(0.0, scaleY * y, width, scaleY * y)
        }
        children += rect
        children += tileGroup
        updateTiles(grid.list)
    }
}
