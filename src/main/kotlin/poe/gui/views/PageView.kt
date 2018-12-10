package poe.gui.views

import javafx.event.EventHandler
import javafx.scene.control.*
import poe.gui.GridPane
import poe.gui.controllers.StashController
import poe.gui.models.Page
import poe.gui.models.PageType
import tornadofx.View
import tornadofx.onChange
import tornadofx.pane

class PageView(val page: Page) : View() {
    val label = Label(page.name)
    val stash : StashController by inject()

    override val root = pane {  }

    val tab = Tab()

    init {
        tab.graphic = label
        tab.content = GridPane(page.grid)
        label.textProperty().bind(page.nameProperty)
        page.gridProperty.onChange {
            if( it!=null ) {
                tab.content = GridPane(it)
            }
        }

        val tabCreate = MenuItem("New")
        tabCreate.onAction = EventHandler {
            stash.createPage(create(stash.pages().size.toString()))
        }

        val tabRename = MenuItem("Rename")
        tabRename.onAction = EventHandler {
            page.name = rename(page.name)
        }

        val typeToggle = ToggleGroup()
        val tabType =
            PageType.values().drop(1).map { type->
                val item = RadioMenuItem(type.displayName)
                item.toggleGroup = typeToggle
                item.onAction = EventHandler {
                    page.pageType = type
                }
                item.isSelected = type==page.pageType
                item
            }.toTypedArray()

        if( page.pageType== PageType.Inventory ) {
            tab.contextMenu = ContextMenu(tabCreate)
        } else {
            tab.contextMenu = ContextMenu(tabRename, tabCreate, SeparatorMenuItem(), *tabType)
        }

    }

    private fun create(name:String) : String {
        val dialog = TextInputDialog(name)
        dialog.title = "New Page"
        dialog.headerText = "Enter name:"
        dialog.contentText = "Name:"
        val result = dialog.showAndWait()
        return result.get()
    }

    private fun rename(name:String) : String {
        val dialog = TextInputDialog(name)
        dialog.title = "Rename"
        dialog.headerText = "Enter new name:"
        dialog.contentText = "Name:"
        val result = dialog.showAndWait()
        return result.get()
    }

}
