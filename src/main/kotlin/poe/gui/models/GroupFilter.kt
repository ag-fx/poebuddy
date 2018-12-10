package poe.gui.models

import poe.model.*
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property

data class MinMaxFilter(val min:Int?, val max:Int?) {
    fun match(v:Int) : Boolean {
        return (min==null || min<=v ) && (max==null || max>=v )
    }

    companion object {
        fun create(it:String?) : MinMaxFilter? {
            return try {
                if( it!=null ) {
                    val fixed = it.toIntOrNull()
                    return if (fixed != null) {
                        MinMaxFilter(fixed, fixed)
                    } else if (it.endsWith("+")) {
                        val min = it.substring(0, it.length - 1).toInt()
                        MinMaxFilter(min, null)
                    } else if (it.startsWith("<")) {
                        MinMaxFilter(null, it.substring(1).toInt())
                    } else if (it.startsWith(">")) {
                        MinMaxFilter(it.substring(1).toInt(), null)
                    } else if (it.contains("-")) {
                        val minMax = it.split("-")
                        MinMaxFilter(minMax[0].toInt(), minMax[1].toInt())
                    } else {
                        null
                    }
                } else {
                    MinMaxFilter(null, null)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class SocketsFilter(val socket: Sockets?) {
    fun match(socket: Sockets?): Boolean {
        if( this.socket==null ) {
            return true
        }
        if( socket==this.socket ) {
            return true
        }

        return socket?.fit(this.socket) ?: false
    }
    companion object {
        fun create(text:String?) : SocketsFilter? {
            return if( text!=null ) {
                val sockets = Sockets.create(text)
                if( sockets!=null ) {
                    SocketsFilter(sockets)
                } else {
                    null
                }
            } else {
                SocketsFilter(null)
            }
        }
    }
}

class GroupFilter(
    type: MainType = MainType.None,
    subType: SubType = SubType.None,
    rarity: Rarity = Rarity.None,
    level:String? = null,
    socket:String? = null,
    name:String? = null,
    text:String? = null) {
    var type by property(type)
    fun typeProperty() = getProperty(GroupFilter::type)
    var subType by property(subType)
    fun subTypeProperty() = getProperty(GroupFilter::subType)
    var rarity by property(rarity)
    fun rarityProperty() = getProperty(GroupFilter::rarity)
    var level by property(level)
    fun levelProperty() = getProperty(GroupFilter::level)

    var socket by property(socket)
    fun socketProperty() = getProperty(GroupFilter::socket)

    var name by property(name)
    fun nameProperty() = getProperty(GroupFilter::name)
    var text by property(text)
    fun textProperty() = getProperty(GroupFilter::text)

    var levelFilter = MinMaxFilter(null, null)
    var socketFilter = SocketsFilter(null)
    init {
        levelProperty().onChange {
            levelFilter = MinMaxFilter.create(it) ?: MinMaxFilter(null, null)
        }
        socketProperty().onChange {
            socketFilter = SocketsFilter.create(it) ?: SocketsFilter(null)
        }
    }

    fun matches(item: Item) : Boolean {
        return (type== MainType.None || type == item.type)
                && (subType== SubType.None || subType==item.subtype)
                && (rarity== Rarity.None || rarity==item.rarity)
                && (matchLevel(item.itemLevel))
                && (matchSocket(item.socket))
                && (name==null || name.isEmpty() || item.name.contains(name, true))
                && (text==null || text.isEmpty() || item.text.contains(text, true))
    }

    fun matchSocket(socket: Sockets?) = socketFilter.match(socket)
    fun matchLevel(level:Int) = levelFilter.match(level)
}
class GroupFilterModel : ItemViewModel<GroupFilter>(){
    val type = bind(GroupFilter::typeProperty)
    val subType = bind(GroupFilter::subTypeProperty)
    val rarity = bind(GroupFilter::rarityProperty)
    val level = bind(GroupFilter::levelProperty)
    val name = bind(GroupFilter::nameProperty)
    val text = bind(GroupFilter::textProperty)
    val socket = bind(GroupFilter::socketProperty)

    val groupModel : GroupConfigModel by inject()
    init {
        dirty.onChange {
            groupModel.listContentDirty.value = true
        }
    }
}