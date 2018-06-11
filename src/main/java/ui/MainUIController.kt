package ui

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

        receiveData()
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



            names.clear()

            // Iterate over all functions that were returned and them to the internal list of functions
            val functions = data["functions"] as JSONArray
            for (i in 0 until functions.size) {
                val function = functions[i] as JSONObject
                names.add(Function(function["name"] as String,
                        function["url"] as String,
                        (function["invocations"] as Long).toInt(),
                        (function["replicas"] as Long).toInt()))
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

class Function(val name: String, val url: String, val invocations: Int, val replicas: Int) {
    override fun toString(): String = name
}
