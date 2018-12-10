package poe.gui

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.stage.Modality
import javafx.stage.Window
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import poe.gui.models.Monitor
import poe.gui.models.MonitorCallback
import poe.gui.models.Stash
import poe.gui.models.pause
import poe.model.*
import tornadofx.runAsync
import java.awt.Robot
import java.awt.event.KeyEvent
import java.util.logging.Level
import java.util.logging.Logger

object Runner {
    var task: Task<Any>? = null
    var debugRun: Boolean = false
    var abortKey: Int = NativeKeyEvent.VC_ESCAPE
    var pauseKey: Int = NativeKeyEvent.VC_ALT
    val robot = Robot()

    val nativeListener = object : NativeKeyListener {
        override fun nativeKeyTyped(p0: NativeKeyEvent?) {
        }

        override fun nativeKeyPressed(p0: NativeKeyEvent) {
        }

        override fun nativeKeyReleased(p0: NativeKeyEvent) {
            if (p0.keyCode == NativeKeyEvent.VC_ESCAPE) {
                println("Stopping")
                task?.cancel(true)
            } else if (p0.keyCode == NativeKeyEvent.VC_ALT) {
                val isPaused = pause.get()
                pause.set(!isPaused)
            }
        }
    }

    fun runAbortable(op: () -> Unit, finished: () -> Unit) {
        val logger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
        logger.level = Level.WARNING
        logger.useParentHandlers = false

        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(nativeListener)

        task = runAsync {
            try {
                op()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        task?.onFailed = EventHandler {
            GlobalScreen.removeNativeKeyListener(nativeListener)
            GlobalScreen.unregisterNativeHook()
            finished()
        }
        task?.onSucceeded = EventHandler {
            GlobalScreen.removeNativeKeyListener(nativeListener)
            GlobalScreen.unregisterNativeHook()
            finished()
        }
        task?.onCancelled = EventHandler {
            GlobalScreen.removeNativeKeyListener(nativeListener)
            GlobalScreen.unregisterNativeHook()
            robot.keyRelease(KeyEvent.VK_CONTROL)
            finished()
        }
    }
}


fun Stash.runWithController(message: String, controller: PoEController?, window: Window, op: (Monitor?) -> Unit) {
    if (controller == null) {
        op(null)
    } else {
        val progressBar = ProgressBar()
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.initModality(Modality.APPLICATION_MODAL)
        alert.initOwner(window)
        alert.headerText = message
        alert.dialogPane.content = progressBar
        val textArea = TextArea()
        alert.dialogPane.expandableContent = textArea
        alert.dialogPane.isExpanded = true
        alert.show()
        val monitor = Monitor(object : MonitorCallback {
            override fun state(text: String) {

            }

            override fun progress(progress: Double) {
                Platform.runLater {
                    progressBar.progress = progress
                }
            }

            override fun info(text: String) {
                Platform.runLater {
                    textArea.appendText("$text\n")
                    textArea.scrollTop = Double.MAX_VALUE
                }
            }
        })

        Runner.runAbortable( {
            this.controller = controller
            controller.switchTo()

            op(monitor)
        }, {
            this.controller = null
            Platform.runLater {
                monitor.info("Finished")
            }
        })
    }
}

