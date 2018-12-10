package poe.gui.controllers

import javafx.beans.property.SimpleObjectProperty
import poe.gui.models.Page
import poe.gui.models.PageType
import poe.gui.models.Stash
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property
import tornadofx.*

class StashController : Controller() {
    val stashProperty = SimpleObjectProperty<Stash>(Stash())
    var stash by stashProperty

    fun pages() = stash.pages

    fun createPage(name:String) : Page {
        val page = Page(name, PageType.Size12x12)
        val index = stash.pages.indexOf(page)
        stash.pages.add(index+1, page)
        return page
    }
}