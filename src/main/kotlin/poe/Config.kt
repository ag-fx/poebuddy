package poe

import poe.gui.models.Rect
import poe.model.*

val ApplicationName = "PoEBuddy"

object PoEConstants {

    val tabRect = Rect(15.0 / 1920.0, 175.0 / 1200.0, 708.0 / 1920.0, 708.0 / 1200)
    val invRect = Rect(1200.0 / 1920.0, 653.0 / 1200.0, 708.0 / 1920.0, 294.0 / 1200)
    val invWidth = 12
    val invHeight = 5
}

fun groupConfig2() : Groups {
    return Groups(listOf(

        Group("Inventory", GridArea(0,0, 12, 5), "Quest") {it.type==MainType.Quest},
        Group("1", GridArea(0,0,6,12), "Quest") {it.subtype==SubType.Jewel},
        Group("1", GridArea(6,0,6,12), "Quest") {it.subtype==SubType.Red}
/*
        Group("16", 0, "4Links") {
            it.socket.length >= 8
            linked(it.socket, 2, 0, 2) // 4L
//            || linked(it.item.socket, 0, 0, 2)
                    || linked(it.socket, 2, 1, 0) // Shield
                    || linked(it.socket, 0, 0, 3)
                    || linked(it.socket, 3, 0, 0)
                    || linked(it.socket, 1, 1, 0)
//                    || ((it.rarity == Rarity.Magic || it.rarity == Rarity.Rare) && (it.resistance.cold > 0 || it.resistance.fire > 0 || it.resistance.lightning > 0))
            || (it.name.contains("Hatred"))
            || (it.name.contains("Generosity"))
            || (it.name.contains("Stone Golem"))
            || (it.name.contains("Immortal Call"))
            || (it.name.contains("Cast when damage taken", true))
            || (it.name.contains("faster casting", true))
            || (it.name.contains("flame dash", true))
            || (it.name.contains("multistrike", true))
            || (it.name.contains("minion damage", true))
            || (it.name.contains("melee splash", true))
            || (it.name.contains("raging spirits", true))
            || (it.name.contains("desecrate", true))
            || (it.name.contains("arctic armour", true))
            || (it.name.contains("faster attacks", true))
            || (it.name.contains("fortify", true))
            || (it.name.contains("shield charge", true))
            || (it.name.contains("minion life", true))
            || (it.name.contains("haste", true))
            || (it.name.contains("convocation", true))
            || (it.name.contains("bone offering", true))
            || (it.name.contains("flesh offering", true))
            || (it.name.contains("enfeeble", true))
            || (it.name.contains("ball lightning", true))
            || (it.name.contains("curse on hit", true))
            || (it.name.contains("projectile weakness", true))
            || (it.name.contains("pierce", true))
            || (it.name.contains("slower projectiles", true))
            || (it.name.contains("spell echo", true))
            || (it.name.contains("controlled destruction", true))
            || (it.name.contains("raise spectres", true))
            || (it.name.contains("faster projectiles", true))
            || (it.name.contains("multiple projectiles", true))
        },

        Group("1", 0, "Red Gems") { it.rarity == Rarity.Gem && it.socket == "R" },
        Group("2", 0, "Green Gems") { it.rarity == Rarity.Gem && it.socket == "G" },
        Group("3", 0, "Blue Gems") { it.rarity == Rarity.Gem && it.socket == "B" },
        Group("4", 0, "Rare Rings") { it.subtype == SubType.Ring && it.rarity == Rarity.Rare },
        Group("4", 6, "Rare Amulets") { it.subtype == SubType.Amulet && it.rarity == Rarity.Rare },
        Group("5", 0, "Jewels") { it.subtype == SubType.Jewel },
        Group("5", 6, "White Gems") { it.rarity == Rarity.Gem && it.socket == "W" },
        Group("6", 0, "Level 1") { it.rarity == Rarity.Rare && it.size().x == 1 },
        Group(
            "7",
            0,
            "Level 2x2"
        ) { (it.subtype == SubType.Shield || it.subtype == SubType.Claw) && it.rarity == Rarity.Rare && it.size().x == 2 },
        Group("8", 0, "Level 2x2") { it.subtype == SubType.Glove && it.rarity == Rarity.Rare },
        Group("9", 0, "Level 2x2") { it.subtype == SubType.Belt && it.rarity == Rarity.Rare },
        Group("10", 0, "Level 2x2") { it.subtype == SubType.Boot && it.rarity == Rarity.Rare },
        Group("12", 0, "Level 2x2") { it.subtype == SubType.Helmet && it.rarity == Rarity.Rare },
        Group(
            "13",
            0,
            "Level 2x3"
        ) { it.rarity == Rarity.Rare && it.size().x == 2 && it.size().y == 3 },
        Group(
            "14",
            0,
            "Level 2x4"
        ) { it.rarity == Rarity.Rare && it.size().x == 2 && it.size().y == 4 },
        Group("15", 0, "Unique") { it.rarity == Rarity.Unique },


        Group("11", 0, "Sell") { it.size().x == 1 },
        Group("11", 7, "Sell") { true }
*/
    ))
}

