package ui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import tornadofx.*

class DeployNewFunctionUI : View() {
    private val controller: DeployNewFunctionController by inject()

    override val root = hbox {
        val selectedFile = SimpleStringProperty()
        val name = SimpleStringProperty()
        val replicas = SimpleStringProperty()

        title = "Deploy New Function"

        gridpane {
            hgap = 20.0
            vgap = 10.0

            label("Name:") {
                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                gridpaneConstraints {
                    columnRowIndex(0, 0)
                }
            }

            textfield {
                textProperty().bindBidirectional(name)

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                gridpaneConstraints {
                    columnRowIndex(1, 0)
                }
            }

            label("Replicas:") {
                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                gridpaneConstraints {
                    columnRowIndex(0, 1)
                }
            }

            textfield {
                textProperty().bindBidirectional(replicas)

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                gridpaneConstraints {
                    columnRowIndex(1, 1)
                }
            }

            button("Select file") {
                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                action {
                    val fileChooser = FileChooser()
                    val file = fileChooser.showOpenDialog(null)

                    if (file != null) {
                        selectedFile.set(file.absolutePath)
                    }
                }

                gridpaneConstraints {
                    columnRowIndex(0, 2)
                }
            }

            label("File:") {
                textProperty().bind(selectedFile)

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                }

                gridpaneConstraints {
                    columnRowIndex(1, 2)
                }
            }

            separator(Orientation.HORIZONTAL) {
                gridpaneConstraints {
                    columnRowIndex(0, 3)
                    columnSpan = 2
                }
            }

            button("Deploy") {
                hgrow = Priority.ALWAYS
                maxWidth = Double.MAX_VALUE
                prefHeight = 60.0

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                    backgroundColor += c("#79B0C0")
                    textFill = c("#FFFFFF")
                    fontWeight = FontWeight.BOLD
                }

                gridpaneConstraints {
                    columnRowIndex(0, 4)
                    columnSpan = 2
                }

                action {
                    controller.deploy(name.value, replicas.value, selectedFile.value)
                }
            }
        }
    }
}
