package poe.gui.models

import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import javafx.collections.FXCollections
import poe.model.Groups
import poe.model.Item
import poe.model.PoEController
import poe.model.Tile
import tornadofx.ViewModel
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property
import java.io.FileReader
import java.io.FileWriter
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicBoolean

data class Point(val x:Int, val y:Int)
data class Rect(val x:Double = 0.0, val y:Double = 0.0, val width:Double = 0.0, val height:Double = 0.0)

val pause = AtomicBoolean(false)

interface  MonitorCallback {
    fun state(text:String)
    fun progress(progress:Double)
    fun info(text:String)
}

class Monitor(var callback : MonitorCallback? = null) {
    var progress:Double=0.0
    var total : Double = 0.0

    fun state(text: String) {
        callback?.state(text)
    }

    fun update(step:Double) {
        this.progress += step
        callback?.progress(progress/total)
    }

    fun info(text: String) {
        callback?.info(text)
    }
}


data class Movement(val from: Tile, val to: Tile)

class Stash(
    pages : List<Page> = emptyList()
) {
    val pages = FXCollections.observableArrayList(pages)

    var controller: PoEController? = null
    val gson = GsonBuilder().setPrettyPrinting().registerTypeAdapter<Page>(Page.serializer).create()
    var currentPage: Int = 0

    var page by property<Page>()
    fun pageProperty() = getProperty(Stash::page)

    init {
        pageProperty().onChange {
            currentPage = this.pages.indexOf(page)
        }

    }

    fun empty() : Stash {
        return Stash(pages.map { Page(it.name, it.pageType) })
    }
    fun save(filename:String) {
        val writer = FileWriter(filename)
        gson.toJson(pages, writer)
        writer.close()
    }

    fun load(filename: String) {
        val input = gson.fromJson<List<Page>>(FileReader(filename))
        pages.clear()
        pages.addAll(input)
    }

    fun scanPage(page: Int, monitor: Monitor?) {
        moveToFirstPage()
        moveToPage(page)
        pages[page].scan(controller, monitor)
    }

    fun scanAll(monitor: Monitor? = null) {
        moveToFirstPage()
        monitor?.total = pages.sumByDouble { it.pageType.width*it.pageType.height.toDouble() }
        pages.forEachIndexed { i, it->
            monitor?.info("Scanning ${it.name}")
            do {
                it.clear()
                val success = try {
                    it.scan(controller, monitor)
                    true
                } catch (e: SanityCheckFailed) {
                    monitor?.info("Scan page failed! Retrying...")
                    Thread.sleep(1000)
                    false
                }
            } while (!success)

            if( i>0 ) {
                nextPage()
            }
        }
    }

    fun sort(groups: Groups) : Pair<Stash, List<Movement>> {
        val copy = Stash(pages.map { Page(it.name, it.pageType) })

        pages.flatMap {
            it.list
        }.forEach { tile ->
            val group = groups.groups.firstOrNull() { it.filter(tile.item) }
            if( group!=null ) {
                group.items += tile
            }
        }
        val movements = mutableListOf<Movement>()
        groups.groups.forEach { group ->
            group.sort()
            println("${group.name} ${group.items.size} ${group.area()}")
            group.items.forEach { tile ->
                val page = copy.pages.first { it.name == group.tab }
                val newTile = page.insert(group.area, tile.item)
                if (newTile != null) {
                    if (newTile.x == tile.x && newTile.y == tile.y && newTile.page.name == tile.page.name) {

                    } else {
                        movements += Movement(tile, newTile)
                    }
                }
            }
        }
        return Pair(copy, movements)
    }

    fun executeMovements(movements:List<Movement>, monitor: Monitor? = null) {
        monitor?.info( "Sorting")
        moveToFirstPage()
        val openList = movements.toMutableList()
        var current : Tile? = null
        var emptyHand = true
        monitor?.total = openList.size.toDouble()
        while (openList.isNotEmpty()) {
            pause.set(false)

            monitor?.update(1.0)
            val m = if( current==null ) {
                emptyHand = true
                openList.removeAt(0)
            } else {
                val mm = openList.find { it.from==current }
                if( mm==null ) {
                    throw IllegalStateException("No target found for ${current.item.name}")
                } else {
                    openList.remove(mm)
                    mm
                }
            }

            val fromTab = pages.first {it.name==m.from.page.name}
            val toTab = pages.first {it.name==m.to.page.name}

            if( emptyHand ) {
                moveAndClick(m.from)
                fromTab.grid.pickup(m.from)
                current = m.from
            }

            if( toTab.grid.canPlaced(m.to) ) {
                monitor?.info("${m.from.page.name} ${m.from.x}, ${m.from.y} -> ${m.to.page.name} ${m.to.x}, ${m.to.y} ${m.from.item.name}")
                current = toTab.grid.place(m.to)
                if( current!=null ) {
                    monitor?.info("  picked up ${current.page.name} ${current.x} ${current.y} ${current.item.name}")
                }

                moveAndClick(m.to)
                emptyHand = false

            } else {
                if( current!=null ) {
                    val temporary = placeTemporary(current.item)
                    if( temporary!=null ) {
                        moveAndClick(temporary)
                        monitor?.info("  using temp space for ${current.item.name} ${temporary.page} ${temporary.x}, ${temporary.y} ")

                        openList += Movement(temporary, m.to)
                        monitor?.total = monitor?.total?: 0.0 + 1.0
                        current = null
                        emptyHand = true
                    } else {
                        throw IllegalStateException("Cant find temp space for ${current.item}")
                    }
                }
            }
        }
    }

    fun nextPage() {
        currentPage++
        if( currentPage>=pages.size ) {
            currentPage = pages.size-1
        }
        page = pages[currentPage]
        controller?.right()
    }
    fun prevPage() {
        controller?.left()
        currentPage--
        if( currentPage<1 ) {
            currentPage = 1
        }
        page = pages[currentPage]
    }

    private fun moveToPage(page:Int) {
        if( page==0 ) {
            return
        }
        if (currentPage < page) {
            (currentPage until page).forEach { controller?.right() }
        } else if (currentPage > page) {
            (page until currentPage).forEach { controller?.left() }
        }
        currentPage = page
        this.page = pages[currentPage]
    }

    private fun moveToFirstPage() {
        (0 until pages.size ).forEach {
            controller?.left()
        }
        currentPage = 1
        page = pages[currentPage]
    }

    private fun placeTemporary(item : Item) : Tile? {
        pages.forEach {
            val found = it.insert(item)
            if( found!=null ) {
                return found
            }
        }
        return null
    }

    private fun moveAndClick(tile: Tile) {
        val tab = pages.first {it.name==tile.page.name}
        val page = pages.indexOf(tab)

        moveToPage(page)
        clickTile(tile)
    }

    private fun clickTile(tile: Tile) {
        val size = tile.item.size()

        val sx = tile.x
        val sy = tile.y
        val tx = tile.x+size.x-1
        val ty = tile.y+size.y-1

        val startX = tile.page.pageType.startX
        val startY = tile.page.pageType.startY
        val cellWidth = tile.page.pageType.cellWidth
        val cellHeight = tile.page.pageType.cellHeight

        val mouseX = ((startX + cellWidth * sx) + (startX + cellWidth*tx))/2.0
        val mouseY = ((startY + cellHeight * sy) + (startY + cellHeight*ty))/2.0

        controller?.moveMouse(mouseX, mouseY)

//        while( pause.get() ) {
            Thread.sleep(100)
//        }
        controller?.mouseClick()
    }


}

