package poe.gui.controllers

import javafx.collections.FXCollections
import poe.gui.models.GroupConfig
import poe.model.Items
import tornadofx.Controller

class GroupController : Controller() {
    val groups = FXCollections.observableArrayList<GroupConfig>()
    init {
        groups.addAll(listOf(GroupConfig("X"), GroupConfig("Y")))

        recalc()
    }

    fun recalc() {
        groups.forEach {
            it.items = 0
        }
        Items.itemList.forEach { item->
            val group = groups.firstOrNull { it.matches(item) }
            if( group!=null ) {
                group.items ++
            }
        }
    }

    fun addGroup() {
        groups.add(GroupConfig("New group"))
    }

    fun removeGroup(groupConfig: GroupConfig?) {
        if( groupConfig!=null ) {
            groups.removeAll(groupConfig)
        }
    }
}