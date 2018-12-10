package poe.gui.views

import javafx.collections.FXCollections
import javafx.util.Callback
import javafx.util.converter.IntegerStringConverter
import poe.gui.GridSelectionEvent
import poe.gui.controllers.GroupController
import poe.gui.controllers.StashController
import poe.gui.models.*
import poe.model.MainType
import poe.model.Rarity
import poe.model.SubType
import tornadofx.*

class GroupConfigTable : View(){
    val ctrl : GroupController by inject()
    val model : GroupConfigModel by inject()

    init {
    }

    override val root = vbox {
        buttonbar {
            button("Add").action {
                ctrl.addGroup()
            }
            button("Remove").action {
                ctrl.removeGroup(model.item)
            }

        }
        tableview(ctrl.groups) {
            title = "Config Groups"
            sortPolicy = Callback {false}
            column("Name", GroupConfig::nameProperty)
            column("Page", GroupConfig::pageProperty)
            column("Items", GroupConfig::itemsProperty)
            bindSelected(model)
        }
    }
}

class GroupConfigEditor : View() {
    val groups : GroupController by inject()
    val model : GroupConfigModel by inject()
    val stash: StashController by inject()
    val filters = FXCollections.observableArrayList<GroupFilterModel>()

    init {
        model.itemProperty.onChange {
            if( it!=null ) {
                filters.setAll(it.filters.map {
                    val m = GroupFilterModel()
                    m.item = it
                    m
                })
            }
        }
        val action : EventContext.(GridSelectionEvent)->Unit = { it->
            model.x.value = it.area.x
            model.y.value = it.area.y
            model.width.value = it.area.width
            model.height.value = it.area.height

            model.page.value = stash.stash.page
        }
        FX.eventbus.subscribe<GridSelectionEvent>(scope, FXEventRegistration(GridSelectionEvent::class, this, null, action as EventContext.(FXEvent) -> Unit))
    }
    override val root = vbox {
        textfield(model.name)
        squeezebox(true) {
            fold("Area", true) {
                form {
                    fieldset {
                        field("Page") {
                            combobox(model.page, stash.stash.pages)
                        }
                        hbox(20) {
                            field("X") {
                                textfield(model.x, IntegerStringConverter()) {
                                    prefWidth = 30.0
                                }
                            }
                            field("Y") {
                                textfield(model.y, IntegerStringConverter()) {
                                    prefWidth = 30.0

                                }
                            }
                            field("W") {
                                textfield(model.width, IntegerStringConverter()) {
                                    prefWidth = 30.0

                                }
                            }
                            field("H") {
                                textfield(model.height, IntegerStringConverter()) {
                                    prefWidth = 30.0

                                }
                            }
                        }
                    }
                }
            }
        }
        button("Add").action {
            val m = GroupFilterModel()
            m.item = GroupFilter()
            model.listContentDirty.value = true
            filters.add(m)
        }
        listview(filters) {
            cellFormat {
                graphic = squeezebox {
                    fold(expanded = true) {
                        form {
                            fieldset {
                                field("Type"){
                                    combobox(it.type, MainType.values().sortedBy { it.displayName }.toList())
                                }
                                field("Subtype"){
                                    combobox(it.subType, SubType.values().sortedBy { it.displayName }.toList())
                                }
                                field("Rarity") {
                                    combobox(it.rarity, listOf(Rarity.None, Rarity.Normal,Rarity.Magic, Rarity.Rare, Rarity.Unique))
                                }
                                field("Name") {
                                    textfield(it.name)
                                }
                                field("Level") {
                                    textfield(it.level).validator {
                                        if( MinMaxFilter.create(it)==null ) error("Wrong level syntax") else null
                                    }
                                }
                                field("Socket") {
                                    textfield(it.socket).validator {
                                        if( SocketsFilter.create(it)==null ) error("Wrong socket syntax") else null
                                    }
                                }
                                field("Text") {
                                    textfield(it.text)
                                }
                            }
                        }
                    }
                }
            }
        }
        buttonbar {
            button("Save") {
                enableWhen(model.dirty.or(model.listContentDirty))
                action {
                    save()
                }
            }

            button("Reset").action {
                model.rollback()
            }
        }
    }

    fun save() {
        model.commit()
        val groupConfig = model.item
        if( groupConfig!=null ) {
            groupConfig.filters.setAll(filters.map {
                it.commit()
                it.item
            })
            model.listContentDirty.value = false
            groups.recalc()
        }
    }

}