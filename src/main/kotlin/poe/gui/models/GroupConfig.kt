package poe.gui.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import poe.model.GridArea
import poe.model.Item
import poe.model.Items
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property


class GroupConfig(name:String?=null, page: Page?=null) {
    var name by property(name)
    fun nameProperty() = getProperty(GroupConfig::name)

    var page by property(page)
    fun pageProperty() = getProperty(GroupConfig::page)

    val area= GridArea()
    fun xProperty() = area.xProperty()
    fun yProperty() = area.yProperty()
    fun widthProperty() = area.widthProperty()
    fun heightProperty() = area.heightProperty()

    var items by property<Int>()
    fun itemsProperty() = getProperty(GroupConfig::items)

    val filters = FXCollections.observableArrayList<GroupFilter>()

    fun matches(item: Item) : Boolean {
        return filters.firstOrNull { it.matches(item) }!=null
    }
}

class GroupConfigModel : ItemViewModel<GroupConfig?>() {
    val name = bind(GroupConfig::nameProperty)
    val page = bind(GroupConfig::pageProperty)
    val x = bind(GroupConfig::xProperty)
    val y = bind(GroupConfig::yProperty)
    val width = bind(GroupConfig::widthProperty)
    val height = bind(GroupConfig::heightProperty)

    val listContentDirty = SimpleBooleanProperty(false)
}
