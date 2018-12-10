package poe.model


data class SocketGroup(val red:Int, val green:Int, val blue:Int, val white:Int) {
    val total = red+green+blue+white

    override fun toString(): String {
        val list = (0 until red).map { "R" } + (0 until green).map { "G" } + (0 until blue).map { "B" } + (0 until white).map { "W" }
        return list.joinToString("-")
    }

    fun fit(other:SocketGroup) : Boolean {
        return red>=other.red && green>=other.green && blue>=other.blue
    }
}
// R-R R
// R R R
class Sockets(val links : List<SocketGroup>) {
    val text : String
    init {
        val sorted = links.sortedByDescending { it.total }
        text = sorted.map {
            it.toString()
        }.joinToString(" ")
    }

    fun fit(sockets: Sockets?) : Boolean {
        if( sockets==null ) {
            return true
        }
        val remaining = this.links.toMutableList()
        sockets.links.sortedByDescending { it.total }.forEach {link->
            remaining.sortBy { it.total }
            var pos = 0
            var found = false
            while(pos<remaining.size) {
                val sg = remaining[pos]
                if( sg.fit(link) ) {
                    remaining[pos] = SocketGroup(sg.red-link.red, sg.green-link.green, sg.blue-link.blue, sg.white-link.white)
                    found = true
                    break
                }
                pos++
            }
            if( !found) {
                return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        return if( other is Sockets )
            text == other.text
        else false
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }

    override fun toString(): String {
        return text

    }

    companion object {
        fun create(desc: String?) : Sockets? {
            if( desc!=null ) {
                val input = desc.toUpperCase()
                input.forEach {
                    if( !(it=='R' || it=='G'  || it=='B' || it=='W' || it==' ' || it=='-')) {
                        return null
                    }
                }
                val len = input.length
                if (len % 2 != 1) {
                    return null
                }
                val groups = input.split(" ")
                return Sockets(groups.map { group ->
                    SocketGroup(group.count { it=='R' }, group.count{ it=='G' }, group.count { it=='B' }, group.count{it=='W'})
                })
            } else {
                return null
            }
        }
    }
}

