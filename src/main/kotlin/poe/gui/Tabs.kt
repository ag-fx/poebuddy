package poe.gui

import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import java.util.concurrent.atomic.AtomicLong

class DraggingTabPaneSupport {

    var currentDraggingTab : Tab? = null
    val draggingId = "DraggingTabPaneSupport-${idGen.incrementAndGet()}"

    fun addSupport(tabPane:TabPane) {
        tabPane.tabs.forEach { addDragHandlers(it) }
        tabPane.tabs.addListener(ListChangeListener { c->
            while (c.next()) {
                if( c.wasAdded() ) {
                    c.addedSubList.forEach { addDragHandlers(it) }
                }
                if( c.wasRemoved()) {
                    c.removed.forEach { removeDragHandlers(it) }
                }
            }
        })
        tabPane.onDragOver = EventHandler { e->
            if( draggingId==e.dragboard.string && currentDraggingTab!=null && currentDraggingTab?.tabPane!=tabPane ) {
                e.acceptTransferModes(TransferMode.MOVE)
            }
        }
        tabPane.onDragDropped = EventHandler { e->
            if( draggingId==e.dragboard.string && currentDraggingTab!=null && currentDraggingTab?.tabPane!=tabPane ) {
                currentDraggingTab?.tabPane?.tabs?.remove(currentDraggingTab)
                tabPane.tabs.add(currentDraggingTab)
                currentDraggingTab?.tabPane?.selectionModel?.select(currentDraggingTab)
            }
        }
    }

    private fun addDragHandlers(tab:Tab) {
        if( (tab.graphic as Label).text=="Inventory" ) {
            return
        }
        val graphic = tab.graphic
        graphic.onDragDetected = EventHandler {
            val db = graphic.startDragAndDrop(TransferMode.MOVE)
            val cc = ClipboardContent()
            cc.putString(draggingId)
            db.setContent(cc)
            db.dragView = graphic.snapshot(null, null)
            currentDraggingTab = tab
        }

        graphic.onDragOver = EventHandler { e->
            if( draggingId==e.dragboard.string && currentDraggingTab!=null && currentDraggingTab?.graphic!=graphic) {
                e.acceptTransferModes(TransferMode.MOVE)
            }
        }

        graphic.onDragDropped = EventHandler { e->
            if( draggingId==e.dragboard.string && currentDraggingTab!=null && currentDraggingTab?.graphic!=graphic) {
                val index = tab.tabPane.tabs.indexOf(tab)
                currentDraggingTab?.tabPane?.tabs?.remove(currentDraggingTab)
                tab.tabPane.tabs.add(index, currentDraggingTab)
                currentDraggingTab?.tabPane?.selectionModel?.select(currentDraggingTab)
            }
        }
        graphic.onDragDone = EventHandler { currentDraggingTab = null }
    }

    private fun removeDragHandlers(tab: Tab) {
        tab.graphic.onDragDetected = null
        tab.graphic.onDragOver= null
        tab.graphic.onDragDropped = null
        tab.graphic.onDragDone = null
    }

    companion object {
        val idGen = AtomicLong()
    }
}