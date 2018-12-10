package poe.gui

import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import poe.ApplicationName
import poe.groupConfig2
import poe.gui.controllers.StashController
import poe.gui.models.Page
import poe.gui.models.PageType
import poe.gui.models.Stash
import poe.gui.views.GroupConfigEditor
import poe.gui.views.GroupConfigTable
import poe.gui.views.ItemTableView
import poe.gui.views.StashView
import poe.model.PoEController
import poe.model.PoEItems
import poe.model.RealPoEController
import poe.model.SimulatedPoEController
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class MainView : View() {
    val items = PoEItems()
    val remoteCtrl = RealPoEController()

    val model: StashController by inject()
    val inventoryFile : File
    val itemTableView = ItemTableView()

    init {
        val appData = Paths.get("${System.getenv("APPDATA")}/$ApplicationName")
        Files.createDirectories(appData)
        inventoryFile = appData.resolve("inv.json").toFile()

        Platform.runLater() {
            setWindowMinSize(900, 600)
            title = ApplicationName
        }
        runAsync {
            val stash = model.stash
            if( inventoryFile.exists() ) {
                stash.load(inventoryFile.path)
            } else {
                stash.pages += Page("Inventory", PageType.Inventory)
                (1..5).forEach {
                    stash.pages += Page("$it", PageType.Size12x12)
                }
            }
        }
    }


    override val root =
        borderpane {
            top = hbox {
                button("Save") {
                    action {
                        val stash = model.stash
                        stash.save(inventoryFile.path)
                    }
                }
                button("Create") {
                    action {
                        val pages = listOf(
                            Page("Inventory", PageType.Inventory),
                            Page("Currency", PageType.Currency),
                            Page("Essence", PageType.Essence),
                            Page("Cards", PageType.Cards)
                        ) + (1..10).map {
                            Page(
                                "$it",
                                PageType.Size12x12
                            )
                        } + Page(
                            "11",
                            PageType.Size24x24
                        ) + (12..17).map { Page("$it", PageType.Size12x12) }
                        model.stash.pages.setAll(pages)
                    }
                }
                button("Connect") {
                    action {
                        val stash = model.stash
                        stash.runWithController("Loading...", remoteCtrl, this.scene.window) {
                            (1..3).forEach {
                                stash.pages.forEach {
                                    stash.prevPage()
                                }
                                stash.pages.forEach {
                                    stash.nextPage()
                                }
                            }
                        }
                    }
                }
                button("Clear inventory")
                button("Scan page") {
                    action {
                        val stash = model.stash
//                        val page = stash.pages[tabpane.selectionModel.selectedIndex]
                        stash.runWithController("Scanning...", remoteCtrl, this.scene.window) {
//                            stash.scanPage(tabpane.selectionModel.selectedIndex, it)
                        }
                    }
                }
                button("Full scan") {
                    action {
                        val stash = model.stash
                        stash.runWithController("Scanning...", remoteCtrl, this.scene.window) {
                            stash.scanAll(it)
                            stash.save("inv4.json")
                        }
                    }
                }
                button("Resort") {
                    action {

                        val stash = model.stash
                        stash.runWithController("Sorting...", remoteCtrl, this.scene.window) {
                            val result = stash.sort(groupConfig2())
                            stash.executeMovements(result.second, it)
                        }
                    }
                }
                button("Simulate full scan") {
                    action {
                        val stash = model.stash
                        val simStash = Stash()
                        simStash.load("inv_all.json")
                        val sim = SimulatedPoEController(simStash)
                        stash.runWithController("Scanning...", sim, this.scene.window) {
                            stash.scanAll(it)
                            stash.save("inv4.json")
                        }
                    }
                }
                button("Simulate Resort") {
                    action {

                        val stash = model.stash
                        val simStash = Stash(stash.pages.map { Page(it.name, it.pageType) })
                        val sim = SimulatedPoEController(simStash)
                        stash.runWithController("Sorting...", sim, this.scene.window) {
                            val result = stash.sort(groupConfig2())
                            stash.executeMovements(result.second, it)
                        }
                    }
                }
            }
            val left = tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab("Items") { itemTableView.root.attachTo(this) }
                tab("Sort Groups") {
                    borderpane {
                        center {
                            GroupConfigTable().root.attachTo(this)
                        }
                        right {
                            GroupConfigEditor().root.attachTo(this)
                        }
                    }
                }
            }
            center = splitpane(Orientation.HORIZONTAL, left, StashView().root)
        }
}

class MainApp : App(MainView::class, Styles::class)

class Styles : Stylesheet() {
    init {

    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}