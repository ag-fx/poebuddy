package poe.model

import poe.gui.models.Rect
import poe.gui.models.Stash
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.io.File
import java.io.FileOutputStream

class CmdOwWrapper() {
    val cmdowFile : File
    val runtime = Runtime.getRuntime()

    init {
        val temp = System.getProperty("java.io.tmpdir")

        cmdowFile = File("$temp/cmdow.exe")
        if( !cmdowFile.exists() ) {
            val input = this.javaClass.getResourceAsStream("/cmdow.exe")
            val out = FileOutputStream(cmdowFile)
            input.copyTo(out)
            out.close()
            input.close()
        }
    }

    fun windowDimensions() : Rect? {
        val args = arrayOf(cmdowFile.path, "\"Path of Exile\"", "/P")
        val process = runtime.exec(args)
        val reader = process.inputStream.reader()
        val message = reader.readLines()
        println(message)
        val regex = Regex("\\w+ [0-9]+\\s+[0-9]+ \\w++ \\w++ \\w++ \\w+\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+).*")
        val result = regex.matchEntire(message[1])
        if (result != null) {
            val left = result.groupValues[1].toDouble()
            val top = result.groupValues[2].toDouble()
            val width = result.groupValues[3].toDouble()
            val height = result.groupValues[4].toDouble()
            return Rect(left, top, width, height)
        } else {
            return null
        }
    }

    fun switchTo() {
        val args = arrayOf(cmdowFile.path, "\"Path of Exile\"", "/ACT")
        val process = runtime.exec(args)
        process.waitFor()
        Thread.sleep(1000)
    }

}
interface PoEController {
    fun switchTo()
    fun moveMouse(x: Double, y: Double)
    fun mouseClick()
    fun ctrlC() : String
    fun left()
    fun right()
}

class SimulatedPoEController(val simStash:Stash) : PoEController {
    val cmdow = CmdOwWrapper()
    var dimensions = Rect()
    var mouseX = 0.0
    var mouseY = 0.0
    override fun switchTo() {
        dimensions = cmdow.windowDimensions()!!
    }

    override fun moveMouse(x: Double, y: Double) {
        mouseX = x*dimensions.width
        mouseY = y*dimensions.height
    }

    override fun mouseClick() {
    }

    override fun ctrlC(): String {
        val page = simStash.pages[simStash.currentPage]
        val pageType = page.pageType
        val x = (((mouseX+2)/dimensions.width-pageType.startX)/pageType.cellWidth).toInt()
        val y = (((mouseY+2)/dimensions.height-pageType.startY)/pageType.cellHeight).toInt()
        val tile = if(page.grid.data.isNotEmpty()) {
            page.grid[x, y]
        } else null
        return if( tile!=null ) tile.item.text else ""
    }

    override fun left() {
        simStash.prevPage()
    }

    override fun right() {
        Thread.sleep(200)
        simStash.nextPage()
    }
}

class RealPoEController() : PoEController {
    val cmdow = CmdOwWrapper()
    val robot = Robot()
    val runtime = Runtime.getRuntime()
    val clipBoard = Toolkit.getDefaultToolkit().systemClipboard
    var dimensions = Rect()

    override fun switchTo() {
        dimensions = cmdow.windowDimensions()!!
        cmdow.switchTo()
    }

    override fun moveMouse(x:Double, y:Double) {
        val posX = (x * dimensions.width).toInt()
        val posY = (y * dimensions.height).toInt()
        robot.mouseMove(posX, posY)
        Thread.sleep(30)
    }

    override fun mouseClick() {
        robot.mousePress(MouseEvent.BUTTON1_MASK)
        robot.mouseRelease(MouseEvent.BUTTON1_MASK)
        Thread.sleep(90)
    }

    override fun ctrlC() : String {
        val transfer = StringSelection("")
        clipBoard.setContents(transfer, transfer)

        robot.keyPress(KeyEvent.VK_CONTROL)
        robot.keyPress(KeyEvent.VK_C)
        Thread.sleep(10)
        robot.keyRelease(KeyEvent.VK_C)
        robot.keyRelease(KeyEvent.VK_CONTROL)
        Thread.sleep(30)
        return try {
            clipBoard.getData(DataFlavor.stringFlavor).toString()
        } catch (e: Exception) {
            ""
        }
    }

    override fun left() {
        robot.keyPress(KeyEvent.VK_LEFT)
        Thread.sleep(20)
        robot.keyRelease(KeyEvent.VK_LEFT)
        Thread.sleep(30)
    }

    override fun right() {
        robot.keyPress(KeyEvent.VK_RIGHT)
        Thread.sleep(20)
        robot.keyRelease(KeyEvent.VK_RIGHT)
        Thread.sleep(30)
    }
}

