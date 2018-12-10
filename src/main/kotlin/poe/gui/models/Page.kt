package poe.gui.models

import com.github.salomonbrys.kotson.typeAdapter
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import poe.PoEConstants
import poe.model.*
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue

enum class PageType(val displayName: String, val width:Int, val height: Int, val rect: Rect) {
    Inventory("Inventory", PoEConstants.invWidth, PoEConstants.invHeight, PoEConstants.invRect),
    Size12x12("12x12", 12, 12, PoEConstants.tabRect),
    Size24x24("24x24", 24, 24, PoEConstants.tabRect),
    Character("Character", 0, 0, Rect()),
    Currency("Currency",  0, 0, Rect()),
    Essence("Essence",  0, 0, Rect()),
    Cards("Cards",  0, 0, Rect());

    override fun toString(): String {
        return displayName
    }

    val cellWidth = rect.width/ width
    val cellHeight = rect.height/ height

    val startX = rect.x + cellWidth/2
    val startY = rect.y + cellHeight/2
}

class SanityCheckFailed() : RuntimeException() {

}

class Page(name: String, pageType: PageType) {
    val nameProperty = SimpleStringProperty(this, "name", name)
    var name by nameProperty

    val pageTypeProperty = SimpleObjectProperty(this, "pageType", pageType)
    var pageType by pageTypeProperty

    val gridProperty = SimpleObjectProperty(this, "grid", Grid(pageType.width, pageType.height))
    var grid by gridProperty

    val list : ObservableList<Tile>
        get() = grid.list

    init {
        pageTypeProperty.onChange {
            if( it!=null ) {
                grid = Grid(it.width, it.height)
            }
        }
    }

    override fun toString(): String {
        return name
    }

    fun clear() {
        grid.clear()
    }
    fun insert(area: GridArea, item: Item) : Tile? {
        return grid.insert(area, item, this)
    }
    fun insert(item: Item) : Tile? {
        return grid.insert(GridArea(0, 0, pageType.width, pageType.height), item, this)
    }

    fun scan(controller: PoEController?, monitor: Monitor? = null) {
        val width = pageType.width
        val height = pageType.height
        (0 until height).forEach { y->
            (0 until width).forEach { x->
                val current = grid[x, y]
                if( current==null ) {
                    val item = readItem(x, y, controller)
                    if (item != null) {
                        val tile = Tile(item, this, x, y)
                        grid.place(tile)
                    }
                } else {
                    val size = current.item.size()
                    if( current.x+size.x-1==x && current.y+size.y-1==y ) {
                        val item = readItem(x, y, controller)
                        if( item!=current.item ) {
                            throw SanityCheckFailed()
                        }
                    }
                }
                monitor?.update(1.0)
            }
        }
    }

    private fun readItem(x: Int, y: Int, controller: PoEController?): Item? {
        val mouseX = pageType.startX + pageType.cellWidth * x
        val mouseY = pageType.startY + pageType.cellHeight * y

        controller?.moveMouse(mouseX, mouseY)
        val content = controller?.ctrlC()
        val item = Items.parseItem(content)
        return item
    }

    companion object {
        val serializer = typeAdapter<Page> {
            write {
                beginObject()
                name("name")
                value(it.name)
                name("type")
                value(it.pageType.name)
                name("items")
                beginArray()
                it.list.forEach {
                    beginObject()
                    name("item")
                    value(it.item.text)
                    name("x")
                    value(it.x)
                    name("y")
                    value(it.y)
                    endObject()
                }
                endArray()
                endObject()
            }
            read {
                beginObject()
                nextName()
                val name = nextString()
                nextName()
                val type = PageType.valueOf(nextString())
                nextName()
                beginArray()
                val page = Page(name, type)
                while( hasNext() ) {
                    beginObject()
                    nextName()
                    val text = nextString()
                    nextName()
                    val x = nextInt()
                    nextName()
                    val y = nextInt()
                    endObject()
                    page.grid.place(Tile(Items.parseItem(text)!!, page, x, y))
                }
                endArray()
                endObject()
                page
            }
        }
    }
}

