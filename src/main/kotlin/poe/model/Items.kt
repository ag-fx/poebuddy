package poe.model

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import javafx.application.Platform
import javafx.collections.FXCollections
import poe.gui.models.Point
import java.io.FileReader

data class ItemInfo(
    val name:String,
    val image:String,
    val type:String,
    val subtype:String,
    val filename: String,
    val w:Int,
    val h:Int
)

object ItemDB {
    val items = Gson().fromJson<List<ItemInfo>>(FileReader("items.json"))

    fun findItem(rarity: Rarity, name:String) : ItemInfo? {
        if( rarity== Rarity.Gem) {
            return items.first { it.type=="gem" && it.name==name }
        }
        if( name.contains("jewel", true)) {
            return items.firstOrNull { it.subtype=="jewel" && name.contains(it.name) }
        }
        return items.firstOrNull { it.type!="gem" && name.contains(it.name) }
    }
}

enum class Rarity {
    None,
    Normal,
    Magic,
    Rare,
    Unique,
    Gem,
    Currency,
    DivinationCard
}


enum class MainType(val id:String, val displayName:String) {
    None("", ""),
    Gem("gem", "Gem"),
    Jewellery("Jewellery", "Jewellery"),
    OneHandedWeapon("one_handed_weapon", "OneHandedWeapon"),
    Offhand("offhand", "Offhand"),
    Armour("armour", "Armour"),
    Flask("Flask", "Flask"),
    TwoHandedWeapon("two_handed_weapon", "TwoHandedWeapon"),
    Quest("quest", "Quest"),
    Other("Other", "Other");

    companion object {

        fun byId(id:String) = values().first { it.id==id }

    }
    override fun toString(): String {
        return displayName
    }
}

enum class SubType(val id:String, val displayName:String) {
    None("", ""),
    Red("red", "Red"),
    Green("green", "Green"),
    Blue("blue", "Blue"),
    Ring("rings", "Ring"),
    Amulet("amulets", "Amulet"),
    Jewel("jewel", "Jewel"),
    Dagger("daggers", "Dagger"),
    ThrustingOneHandedSword("thrusting one hand swords", "ThrustingOneHandedSword"),
    Wand("wands", "Wand"),
    OneHandedSword("one hand swords", "OneHandedSword"),
    Shield("shields", "Shield"),
    Claw("claws", "Claw"),
    Glove("gloves", "Glove"),
    Belt("belts", "Belt"),
    Boot("boots", "Boot"),
    HybridFlask("hybrid flasks", "HybridFlask"),
    BodyArmour("body armours", "BodyArmour"),
    Helmet("helmets", "Helmet"),
    Sceptre("sceptres", "Sceptre"),
    TwoHandedMace("two hand maces", "TwoHandedMace"),
    LifeFlask("life flasks", "LifeFlask"),
    Bow("bows", "Bow"),
    OneHandedMace("one hand maces", "OneHandedMace"),
    TwoHandedSword("two hand swords", "TwoHandedSword"),
    MapFragment("map fragments", "MapFragment"),
    ManaFlask("mana flasks", "ManaFlask"),
    UtilityFlask("utility flasks", "UtilityFlask"),
    Staff("staves", "Staff"),
    OneHandedAxe("one hand axes", "OneHandedAxe"),
    TwoHandedAxe("two hand axes", "TwoHandedAxe"),
    Quiver("quivers", "Quiver"),
    CriticalUtilityFlask("critical utility flasks", "CriticalUtilityFlask"),
    Currency("stackable currency", "Currency"),
    Quest("quest", "Quest"),
    Map("map", "Map");

    companion object {
        fun byId(id:String) = values().first {
            it.id==id
        }
    }

    override fun toString(): String {
        return displayName
    }
}
data class AddToResistance(val lightning:Int, val cold:Int, val fire:Int, val chaos:Int)

data class Item(
    val id:Int,
    val text:String,
    val name:String,
    val type:MainType,
    val subtype:SubType,
    val rarity: Rarity,
    val gemType:String,
    val socket:Sockets?,
    val itemLevel:Int,
    val armour:Int,
    val evation:Int,
    val width:Int,
    val height:Int,
    val filename:String,
    val resistance: AddToResistance
) {
    fun size() : Point {
        return Point(width, height)
    }
}

class PoEItems {
    val cache  = mutableMapOf<String, Item>()
    val itemList = FXCollections.observableArrayList<Item>()

    fun parseItem(content:String?): Item? {
        if( content==null || content=="" ) {
            return null
        }

        return cache.computeIfAbsent(content) {text->
            val resistanceRe = Regex("\\+([0-9]+)% to (Lightning|Cold|Fire|Chaos) Resistance")
            val resistance = mutableMapOf<String,Int>()
            val parts = text.split("--------")
            var partIndex = 0
            val m = parts[partIndex++].split("\n")
            val rarity : Rarity = Rarity.valueOf(m[0].split(":")[1].replace(" ", "").trim())
            val names = m.drop(1)
            val name = names.joinToString(" ").trim()


            var sockets = ""
            var idb = ItemDB.findItem(rarity, name)
            if( idb==null ){
                println("Unknown item, suggesting quest item $name")
                idb = ItemInfo(name, "", "quest", "quest", "", 1, 1)
            }
            var type = idb.type
            if( type=="gem") {
                when(idb.subtype) {
                    "green"->sockets = "G"
                    "blue"->sockets = "B"
                    "red"->sockets = "R"
                    "white"->sockets = "W"
                }
            }
            var gemType = ""
            var itemLevel = 0
            var first = true
            var armour = 0
            var evation = 0
            var filename = idb.filename
            var subtype = idb.subtype
            (partIndex until parts.size).forEach { part ->
                val lines = parts[part].trim().split("\n")
                if (first) {
                    if (rarity == Rarity.Gem) {
                        gemType = lines[0]
                    } else if (name.contains("Flask")) {

                    } else if (!lines[0].contains(":")) {
                    }
                }
                first = false
                if (lines[0] != "Requirements:") {
                    lines.forEach { line ->
                        extract("Item Level", line) { itemLevel = it.toInt() }
                        extract("Sockets", line) { sockets = it }
                        extract("Armour", line) { armour = it.toInt() }
                        extract("Evasion Rating", line) { evation = it.toInt() }
                        val m = resistanceRe.matchEntire(line)
                        if( m!=null ) {
                            val value = m.groupValues[1].toInt()
                            val type = m.groupValues[2].toLowerCase()
                            resistance[type] = value
                        }
                    }
                }
            }
            val r = AddToResistance(
                resistance["lightning"] ?: 0,
                resistance["cold"] ?: 0,
                resistance["fire"] ?: 0,
                resistance["chaos"] ?: 0
            )
            val item  =Item(
                cache.size,
                text,
                name,
                MainType.byId(type),
                SubType.byId(subtype),
                rarity,
                gemType,
                Sockets.create(sockets),
                itemLevel,
                armour,
                evation,
                idb.w,
                idb.h,
                filename,
                r
            )
            Platform.runLater { itemList.add(item) }

            item
        }
    }
}

val Items = PoEItems()

private fun extract(start:String, text:String, cb: (String)->Unit) {
    if( text.contains(": ") && text.startsWith(start)) {
        val assign = text.split(": ")
        val value = assign[1]
        try {
            if( value.contains("(")) {
                cb(value.split("(")[0].trim())
            } else {
                cb(value)
            }
        } catch (e: Exception) {
            println("Cannot parse $value")
        }
    }
}
