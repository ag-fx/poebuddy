package poe.gui.views

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import poe.gui.models.GroupFilter
import poe.gui.models.GroupFilterModel
import poe.gui.models.MinMaxFilter
import poe.gui.models.SocketsFilter
import poe.model.*
import tornadofx.*
import java.util.function.Predicate

class ItemTableView : View() {
    val filter = GroupFilter()
//    var nameField = SimpleStringProperty()
//    var typeField = SimpleObjectProperty<MainType>(MainType.None)
//    var subTypeField = SimpleObjectProperty<SubType>(SubType.None)

    val filteredList = SortedFilteredList<Item>()
    init {
        Items.itemList.addListener(ListChangeListener {
            while(it.next()) {
                if( it.wasAdded() ) {
                    it.addedSubList.forEach { item->
                        filteredList.add(item)
                    }
                }
            }
        })

        filter.nameProperty().onChange {
            updatePredicate()
        }
        filter.typeProperty().onChange {
            updatePredicate()
        }
        filter.subTypeProperty().onChange {
            updatePredicate()
        }
        filter.levelProperty().onChange { updatePredicate() }
        filter.socketProperty().onChange { updatePredicate() }
        filter.rarityProperty().onChange { updatePredicate() }
        filter.textProperty().onChange { updatePredicate() }

    }

    private fun updatePredicate() {
        filteredList.predicate = {
            filter.matches(it)
        }
    }

    override val root = borderpane {
        top = form {
            fieldset {
                field("Type"){
                    combobox(filter.typeProperty(), MainType.values().sortedBy { it.displayName }.toList())
                }
                field("Subtype"){
                    combobox(filter.subTypeProperty(), SubType.values().sortedBy { it.displayName }.toList())
                }
                field("Rarity") {
                    combobox(filter.rarityProperty(), listOf(
                        Rarity.None, Rarity.Normal,
                        Rarity.Magic, Rarity.Rare, Rarity.Unique))
                }
                field("Name") {
                    textfield(filter.nameProperty())
                }
                field("Level") {
                    textfield(filter.levelProperty())
                }
                field("Socket") {
                    textfield(filter.socketProperty())
                }
                field("Text") {
                    textfield(filter.textProperty())
                }
            }
        }
        center = tableview(filteredList) {
            readonlyColumn("Rarity", Item::rarity)
            readonlyColumn("Name", Item::name)
            readonlyColumn("Subtype", Item::subtype)
            readonlyColumn("Level", Item::itemLevel)
            readonlyColumn("Sockets", Item::socket)
            smartResize()
        }
    }

}