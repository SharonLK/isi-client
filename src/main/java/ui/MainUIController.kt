package ui

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import tornadofx.Controller
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainUIController : Controller() {
    val names: ObservableList<Function> = FXCollections.observableArrayList()

    private val serverUrl: String

    private var running = true

    init {
        // Read the config file found in the resources directory
        val file = File(javaClass.classLoader.getResource("config.json").file)
        val parser = JSONParser()
        val json = parser.parse(FileReader(file)) as JSONObject

        // Get the server properties
        val server = json["server"] as String
        val port = (json["port"] as Long).toInt()

        // Create an HTTP URL connection and send a GET request to the server
        serverUrl = "http://$server:$port"

        runAsync(true) {
            while (running) {
                Platform.runLater {
                    receiveData()
                }

                Thread.sleep(3000)
            }
        }

        primaryStage.setOnCloseRequest {
            running = false
        }
    }

    private fun receiveData() {
        val url = URL(serverUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            // Read the JSON file that was returned by the server
            val socketIn = BufferedReader(InputStreamReader(connection.inputStream))
            val response = socketIn.lines().reduce("", { str1, str2 -> str1 + str2 })
            val data = JSONParser().parse(response) as JSONObject

            // Iterate over all functions that were returned and them to the internal list of functions
            val functions = data["functions"] as JSONArray
            for (i in 0 until functions.size) {
                // Retrieve information of the function from the JSON object
                val function = functions[i] as JSONObject
                val name = function["name"]
                val funcUrl = function["url"]
                val invocations = (function["invocations"] as Long).toInt()
                val replicas = (function["replicas"] as Long).toInt()

                // Check if a function with that name already exists in internal DB
                val first = names.firstOrNull { func -> func.name == name }

                if (first == null) {
                    // If function doesn't exist in internal DB, add it
                    names.add(Function(name as String,
                            funcUrl as String,
                            invocations,
                            replicas))
                } else {
                    // If function already exists in internal DB, update amount of invocations
                    first.invocations = invocations
                }
            }

            socketIn.close()
        }
    }

    fun downloadFunction(function: String, file: File) {
        val url = URL("$serverUrl/download")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("name", function)
        connection.requestMethod = "GET"
        connection.doInput = true

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bos.write(connection.inputStream.readBytes())
            bos.close()

            println("ZIP received")
        }
    }
}

class Function(val name: String, val url: String, var invocations: Int, var replicas: Int) {
    override fun toString(): String = name
}
