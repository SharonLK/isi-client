package ui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import tornadofx.*
import java.io.File
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL

fun Controller.postConnectToServer (propertyMap : Map<String, String> = mapOf(),
                                    typeOfRequest : String): HttpURLConnection
{
    // Read the config file found in the resources directory
    val config = File(this.javaClass.classLoader.getResource("config.json").file)
    val parser = JSONParser()
    val json = parser.parse(FileReader(config)) as JSONObject

    // Get the server properties
    val server = json["server"] as String
    val port = (json["port"] as Long).toInt()

    // Create an HTTP URL connection and set a POST request to the server with all needed information
    val url = URL("http://$server:$port/$typeOfRequest")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    propertyMap.forEach { prop, value -> connection.setRequestProperty(prop, value) }
    connection.doOutput = true

    return connection
}

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
    private val selectedInvocations = SimpleStringProperty()
    private val selectedReplicas = SimpleStringProperty()
    private val selectedUrl = SimpleStringProperty()

    override val root = hbox {
        title = "ISI"
        spacing = 20.0

        vbox {
            padding = Insets(20.0, 0.0, 20.0, 20.0)
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
                    selectedInvocations.value = "Invocations: ${it?.invocations}"
                    selectedReplicas.value = "Replicas: ${it?.replicas}"
                }

                selectionModel.select(0)
            }

            button("Deploy New Function") {
                prefHeightProperty().set(60.0)
                maxWidth = Double.MAX_VALUE
                hgrow = Priority.ALWAYS

                style {
                    fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                    backgroundColor += c("#BC8BA4")
                    textFill = c("#FFFFFF")
                    fontWeight = FontWeight.BOLD
                }

                action {
                    openInternalWindow(DeployNewFunctionUI::class)
                }
            }
        }

        separator(Orientation.VERTICAL) {
            padding = Insets(20.0, 0.0, 20.0, 0.0)
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
                    textFill = c("#D87B3C")
                }
            }

            separator(Orientation.HORIZONTAL)

            hbox {
                spacing = 40.0

                label {
                    textProperty().bind(selectedUrl)

                    style {
                        fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                        fontStyle = FontPosture.ITALIC
                        textFill = c("#58564C")
                    }
                }

                separator(Orientation.VERTICAL)

                label {
                    textProperty().bind(selectedInvocations)

                    style {
                        fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                        textFill = c("#58564C")
                    }
                }

                separator(Orientation.VERTICAL)

                label {
                    textProperty().bind(selectedReplicas)

                    style {
                        fontSize = Dimension(1.5, Dimension.LinearUnits.em)
                        textFill = c("#58564C")
                    }
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
                        val fileChooser = FileChooser()
                        val file = fileChooser.showSaveDialog(null)

                        if (file != null) {
                            controller.downloadFunction(selectedName.value, file)
                        }
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

                    action {
                        openInternalWindow(ResubmitFunctionUI::class)
                        find<ResubmitFunctionUI>().apply {
                            name.value = selectedName.value
                        }
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

                    action {
                        val name = mapOf<String, String>("name" to selectedName.value)
                        val connection = controller.postConnectToServer(name, "remove")
                        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                            println("Function removed successfully")
                        }
                    }

                }
            }
        }
    }

}
