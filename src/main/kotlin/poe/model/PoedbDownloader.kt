package poe.model

import com.google.gson.Gson
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import jodd.io.FileUtil
import jodd.io.NetUtil
import jodd.jerry.Jerry
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.IllegalStateException
import javax.imageio.ImageIO


fun createItemInfo(name:String, image:String, type:String, subtype: String) : ItemInfo {
    val filename = name.replace(" ", "").replace("'", "")+".png"
    val file = File("poedb/images/$filename")
    if (!file.exists()) {
        try {
            println("Downloading $image")
            NetUtil.downloadFile(image, file)
        } catch (e: Exception) {
        }
    }
    val img = if( file.exists() ) {
        ImageIO.read(file)
    } else null
    return if( img!=null ) {
        val w = img.width / 47
        val h = img.height / 47
        ItemInfo(name, image, type, subtype, filename, w, h)
    } else ItemInfo(name, image, type, subtype, filename, 1, 1)

}

fun download(query:String) :String {
    val file = File("poedb/${query.replace("?","_").replace("/","_")}.html")
    if( !file.exists() ) {
        println("Downloading $query")
        NetUtil.downloadFile("https://poedb.tw/us/$query", file)
    }
    return FileUtil.readString(file)
}
fun parse(query:String) : Jerry {

    return Jerry.jerry(download(query))
}

fun parseGems(query:String) : List<ItemInfo> {
    val list = mutableListOf<ItemInfo>()
    val doc = parse("gem.php?cn=$query")
    doc.find("tr").forEach {
        val links = it.find("td a")
        if( links.size()==2 ) {
            val image = links[0].childElements[0].getAttribute("src")
            val color = links[1].childElements[0].getAttribute("src")
            val name = links[1].childNodes[1].textContent
            val type = if( color.contains("White")) {
                "white"
            } else if( color.contains("Dexterity")) {
                "green"
            } else if( color.contains("Intelligence")) {
                "blue"
            } else if( color.contains("Strength")) {
                "red"
            } else {
                throw IllegalStateException()
            }
            list += createItemInfo(name, image, "gem", type)
        }
    }
    return list
}

data class ItemClass(val caption:String, val data:List<List<String>>)
fun parseItems(query: String, type:String) : List<ItemInfo> {
    val linkRe = Regex(".*\"(.*?)\".*")
    val nameRe = Regex("<a.*?>(.*?)</a>.*")
    val nameRe2 = Regex("<img.*?>(.*?)</a>.*")
    val doc = download("json.php/item_class?cn=$query")
    val ic = Gson().fromJson<ItemClass>(doc)
    val subtype = ic.caption.split("/")[0].trim().toLowerCase()
    val list = mutableListOf<ItemInfo>()
    ic.data.forEach { line->
        val image = linkRe.matchEntire(line[0])!!.groupValues[1]
        var name = nameRe.find(line[1])!!.groupValues[1]
        if( name.startsWith("<")) {
            name = nameRe2.find(line[1])!!.groupValues[1]
        }
        list += createItemInfo(name, image, type, subtype)
    }
    return list
}

fun parseUniques(query: String, type:String) : List<ItemInfo> {
    val doc = parse("unique.php?cn=$query")
    val list = mutableListOf<ItemInfo>()
    val subtype = query.toLowerCase().replace("+", "_")
    doc.find("tr").forEach {
        val links = it.find("td a")
        if( links.size()>=2) {
            val image = links[0].childElements[0].getAttribute("src")
            val name = links[1].childNodes[0].textContent
            list += createItemInfo(name, image, type, subtype)
        }

    }
    return list
}

fun parseMaps() : List<ItemInfo> {
    val doc = parse("area.php?cn=Map")
    val list = mutableListOf<ItemInfo>()
    doc.find("tr td").forEach {
        val link = it.find("a")
        val imgs = it.find("img")
        if( link.size()==1 ) {
            if( imgs.size()>=1) {
                val name = link[0].textContent
                val image = imgs[0].getAttribute("src")
                list += createItemInfo(name, image, "Other", "map")
            }
        }  else if( link.size()==2 && link[1].childNodes.size==1) {
            val name = link[0].textContent
            val image = link[1].childNodes[0].getAttribute("src")
            if( image!=null ) {
                list += createItemInfo(name, image, "Other", "map")
            }
        }
    }
    return list
}
fun main(args: Array<String>) {


        var items = emptyList<ItemInfo>()
        items += parseGems("Active+Skill+Gem")
        items += parseGems("Support+Skill+Gem")
        mapOf(
            "one_handed_weapon" to listOf(
                "Claw",
                "Dagger",
                "Wand",
                "One+Hand+Sword",
                "Thrusting+One+Hand+Sword",
                "One+Hand+Axe",
                "One+Hand+Mace",
                "Sceptre"
            ),
            "two_handed_weapon" to listOf(
                "Bow",
                "Staff",
                "Two+Hand+Sword",
                "Two+Hand+Axe",
                "Two+Hand+Mace",
                "FishingRod"
            ),
            "offhand" to listOf("Shield", "Quiver", "Two+Hand+Sword", "Two+Hand+Axe", "Two+Hand+Mace"),
            "armour" to listOf("Gloves", "Boots", "Body+Armour", "Helmet"),
            "Jewellery" to listOf("Amulet", "Ring", "Belt"),
            "Flask" to listOf("LifeFlask", "ManaFlask", "HybridFlask", "UtilityFlask", "UtilityFlaskCritical"),
            "Other" to listOf(
                "StackableCurrency",
                "MapFragment",
                "HideoutDoodad",
                "Microtransaction",
                "DivinationCard",
                "MiscMapItem",
                "PantheonSoul",
                "UniqueFragment",
                "Jewel",
                "AbyssJewel"
            ),
            "Lab" to listOf("LabyrinthItem", "LabyrinthTrinket", "LabyrinthMapItem")
        ).forEach { type, list ->
            list.forEach {
                items += parseItems(it, type)
            }
        }
        //
        mapOf(
            "one_handed_weapon" to listOf(
                "Claw",
                "Dagger",
                "Wand",
                "One+Hand+Sword",
                "Thrusting+One+Hand+Sword",
                "One+Hand+Axe",
                "One+Hand+Mace",
                "Sceptre"
            ),
            "two_handed_weapon" to listOf(
                "Bow",
                "Staff",
                "Two+Hand+Sword",
                "Two+Hand+Axe",
                "Two+Hand+Mace",
                "Fishing+Rod"
            ),
            "offhand" to listOf("Shield", "Quiver", "Two+Hand+Sword", "Two+Hand+Axe", "Two+Hand+Mace", "Fishing+Rod"),
            "armour" to listOf("Gloves", "Boots", "Body+Armour", "Helmet"),
            "Jewellery" to listOf("Amulet", "Ring", "Belt"),
            "Flask" to listOf("LifeFlask")
        ).forEach { type, list ->
            list.forEach {
                items += parseUniques(it, type)
            }
        }

        items += parseMaps()

        println(items.size)
        val writer = FileWriter("items.json")
        GsonBuilder().setPrettyPrinting().create().toJson(items, writer)
        writer.close()


}