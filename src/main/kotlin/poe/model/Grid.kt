package poe.model

import javafx.collections.FXCollections
import poe.gui.models.Page
import tornadofx.getProperty
import tornadofx.property
import java.lang.IllegalArgumentException

class GridArea(x:Int=0, y:Int=0, width: Int=0, height:Int=0) {
    var x by property(x)
    fun xProperty() = getProperty(GridArea::x)
    var y by property(y)
    fun yProperty() = getProperty(GridArea::y)
    var width by property(width)
    fun widthProperty() = getProperty(GridArea::width)
    var height by property(height)
    fun heightProperty() = getProperty(GridArea::height)

}

data class Tile(val item: Item, val page: Page, val x:Int, val y:Int)

class Grid(val width:Int, val height:Int) {
    val data = Array<Tile?>(width*height) { null }
    val list = FXCollections.observableArrayList<Tile>()

    operator fun get(x:Int, y:Int) : Tile? = data[x + y*width]
    operator fun set(x:Int, y:Int, tile: Tile?) {
        data[x + y*width] = tile
    }

    fun clear() {
        list.clear()
        if(data.isNotEmpty()) {
            data.fill(null, 0, data.size - 1)
        }
    }

    fun canPlaced(tile: Tile) : Boolean{
        val itemSize = tile.item.size()
        val current = mutableSetOf<Tile>()
        (0 until itemSize.y).forEach { j->
            (0 until itemSize.x).forEach { i->
                val t = this[tile.x + i, tile.y + j]
                if( t!=null && t!=tile) {
                    current += t
                }
            }
        }
        return current.size<=1
    }

    fun place(tile: Tile) : Tile? {
        if( canPlaced(tile) ) {
            val itemSize = tile.item.size()
            list += tile
            var current : Tile? = null
            (0 until itemSize.y).forEach { j->
                (0 until itemSize.x).forEach { i->
                    val t = this[tile.x + i, tile.y + j]
                    if( t!=null && t!=tile) {
                        current = t
                    }
                }
            }
            if( current!=null ) {
                list.remove(current!!)
                val oldSize = current!!.item.size()
                (0 until oldSize.y).forEach { j ->
                    (0 until oldSize.x).forEach { i ->
                        this[current!!.x+i, current!!.y+j] = null
                    }
                }
            }
            (0 until itemSize.y).forEach { j ->
                (0 until itemSize.x).forEach { i ->
                    this[tile.x + i, tile.y + j] = tile
                }
            }
            return current
        } else {
            throw IllegalArgumentException()
        }
    }

    fun insert(area: GridArea, item: Item, page: Page) : Tile? {
        val size = item.size()

        val width = area.width + area.x-size.x
        val height = area.height + area.y-size.y
        (area.x..width).forEach { x->
            (area.y..height).forEach { y->
                var free = true
                (0 until size.x).forEach { i->
                    (0 until size.y).forEach { j->
                        if( this[x+i, y+j]!=null ) {
                            free = false
                        }
                    }
                }
                if( free ) {
                    val tile = Tile(item, page, x, y)
                    place(tile)
                    return tile
                }
            }
        }
        return null
    }

    fun pickup(tile: Tile) {
        val itemSize = tile.item.size()
        list -= tile
        (0 until itemSize.y).forEach { j->
            (0 until itemSize.x).forEach { i->
                this[tile.x + i, tile.y + j] = null
            }
        }
    }
}