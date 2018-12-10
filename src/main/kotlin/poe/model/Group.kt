package poe.model


data class Group(
    val tab:String,
    val area: GridArea,
    val name:String,
    val filter:(Item)->Boolean
) {
    val items = mutableListOf<Tile>()
    fun sort() {
        items.sortBy {
            "${it.item.type}_${it.item.subtype}_${it.item.rarity.ordinal}_${it.item.itemLevel}_${it.item.name}"
        }
    }
    fun area() : Int {
        return items.sumBy {
            it.item.size().x*it.item.size().x
        }
    }
}

class Groups(val groups:List<Group>) {

}

fun linked(sockets:String, needRed:Int, needGreen:Int, needBlue:Int) : Boolean {
    val no = needBlue+needGreen+needRed
    val links = sockets.sumBy { if( it=='-' ) 1 else 0  } + 1
    val reds = sockets.sumBy { if( it=='R' ) 1 else 0  }
    val greens = sockets.sumBy { if( it=='G' ) 1 else 0  }
    val blues = sockets.sumBy { if( it=='B' ) 1 else 0  }
    val whites = sockets.sumBy { if( it=='W' ) 1 else 0  }
    return links == no && needRed==reds && needGreen==greens && needBlue==blues
}
