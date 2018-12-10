package model

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import poe.model.Sockets

class SocketsTests : StringSpec({
    "illegal desc" {
        Sockets.create("") shouldBe null
        Sockets.create(null) shouldBe null
        Sockets.create("X") shouldBe null
        Sockets.create("RX") shouldBe null
        Sockets.create("R+X") shouldBe null
        Sockets.create("R-RX") shouldBe null
        Sockets.create("R X") shouldBe null
    }
    "should work " {
        Sockets.create("R-R") shouldNotBe null
        Sockets.create("R-R G") shouldNotBe null
        Sockets.create("R R G") shouldNotBe null
        Sockets.create("R   R G") shouldNotBe null
        Sockets.create("R   R-G") shouldNotBe null
    }
    "equal should work" {
        Sockets.create("R-G-B W") shouldBe Sockets.create("W B-R-G")
    }
    "fit should work" {
        Sockets.create("R")!!.fit(Sockets.create("R")!!).shouldBe(true)
        Sockets.create("R")!!.fit(Sockets.create("G")!!).shouldNotBe(true)
        Sockets.create("R-G")!!.fit(Sockets.create("G")!!).shouldBe(true)
        Sockets.create("R-G")!!.fit(Sockets.create("R")!!).shouldBe(true)
        Sockets.create("R-G")!!.fit(Sockets.create("G-R")!!).shouldBe(true)
        Sockets.create("R-G B-B-B")!!.fit(Sockets.create("G-R")!!).shouldBe(true)
        Sockets.create("R-G B-B-B")!!.fit(Sockets.create("G-R B-B-b")!!).shouldBe(true)
        Sockets.create("R-G B-B-B")!!.fit(Sockets.create("G-R B-B")!!).shouldBe(true)
        Sockets.create("R-G B-B-B")!!.fit(Sockets.create("G-R-B B-B")!!).shouldNotBe(true)
        Sockets.create("R-G B-B-B")!!.fit(null).shouldBe(true)
    }
})