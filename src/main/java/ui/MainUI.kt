package ui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import tornadofx.*

fun main(args: Array<String>) {
    launch<Screen>(args)
}

class Screen : App(HelloWorld::class, InternalWindow.Styles::class) {
    override fun start(stage: Stage) {
        super.start(stage)

        stage.width = 1280.0
        stage.height = 720.0

        stage.show()
    }
}

class HelloWorld : View() {
    private val controller: MainUIController by inject()
    private val selectedName = SimpleStringProperty()
    private val selectedUrl = SimpleStringProperty()

    override val root = hbox {
        spacing = 20.0

        vbox {
            padding = Insets(20.0)
            spacing = 20.0

            listview<Function> {
                vgrow = Priority.ALWAYS
                itemsProperty().set(controller.names)

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                selectionModel.selectedItemProperty().onChange {
                    selectedName.value = it?.name
                    selectedUrl.value = it?.url
                }

                selectionModel.select(0)
            }

            button("Deploy New Function") {
                prefHeightProperty().set(60.0)
                maxWidth = Double.MAX_VALUE
                hgrow = Priority.ALWAYS

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                    backgroundColor += c("#D87B3C")
                    textFill = c("#FFFFFF")
                    fontWeight = FontWeight.BOLD
                }

                action {
                    openInternalWindow(DeployNewFunctionUI::class)
                }
            }
        }

        vbox {
            spacingProperty().set(10.0)
            paddingProperty().set(Insets(10.0, 20.0, 10.0, 0.0))
            hgrow = Priority.ALWAYS

            label {
                textProperty().bind(selectedName)

                style {
                    fontSize = Dimension(4.0, Dimension.LinearUnits.em)
                    fontWeight = FontWeight.BOLD
                    textFill = c("#282828")
                }
            }

            label {
                textProperty().bind(selectedUrl)

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                    fontStyle = FontPosture.ITALIC
                    textFill = c("#58564C")
                }
            }

            separator(Orientation.HORIZONTAL)

            separator(Orientation.HORIZONTAL)

            hbox {
                spacingProperty().set(20.0)

                button("Download") {
                    prefHeightProperty().set(60.0)
                    prefWidthProperty().set(150.0)
                    style {
                        fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                        backgroundColor += c("#79B0C0")
                        textFill = c("#FFFFFF")
                        fontWeight = FontWeight.BOLD
                    }

                    action {
                        controller.downloadFunction(selectedName.value)
                    }
                }

                button("Re-submit") {
                    prefHeightProperty().set(60.0)
                    prefWidthProperty().set(150.0)
                    style {
                        fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                        backgroundColor += c("#7EAB75")
                        textFill = c("#FFFFFF")
                        fontWeight = FontWeight.BOLD
                    }
                }

                pane {
                    hgrow = Priority.ALWAYS
                }

                button("Remove") {
                    prefHeightProperty().set(60.0)
                    prefWidthProperty().set(150.0)
                    style {
                        fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                        backgroundColor += c("#AE4338")
                        textFill = c("#FFFFFF")
                        fontWeight = FontWeight.BOLD
                    }
                }
            }
        }
    }
}
