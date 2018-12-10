package poe.gui.views

import poe.gui.DraggingTabPaneSupport
import poe.gui.controllers.StashController
import tornadofx.View
import tornadofx.bind
import tornadofx.onChange
import tornadofx.tabpane

class StashView: View() {
    val model : StashController by inject()

    override val root = tabpane {
        model.stash.pageProperty().onChange {
            selectionModel.select(tabs[model.stash.currentPage])
        }
        tabs.bind(model.stash.pages) { it ->
            PageView(it).tab
        }

        selectionModel.selectedItemProperty().onChange {
            if( it!=null ) {
                model.stash.page = model.stash.pages[tabs.indexOf(it)]
            }
        }
    }

    init {
        DraggingTabPaneSupport().addSupport(root)
    }
}
