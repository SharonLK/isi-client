package ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import tornadofx.Controller
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainUIController : Controller() {
    val names: ObservableList<Function> = FXCollections.observableArrayList()

    init {
        // Read the config file found in the resources directory
        val file = File(javaClass.classLoader.getResource("config.json").file)
        val parser = JSONParser()
        val json = parser.parse(FileReader(file)) as JSONObject

        // Get the server properties
        val server = json["server"] as String
        val port = (json["port"] as Long).toInt()

        // Create an HTTP URL connection and send a GET request to the server
        val url = URL("http://$server:$port")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            // Read the JSON file that was returned by the server
            val socketIn = BufferedReader(InputStreamReader(connection.inputStream))
            val response = socketIn.lines().reduce("", { str1, str2 -> str1 + str2 })
            val data = parser.parse(response) as JSONObject

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

    fun downloadFunction(function: String) {
        println(function)
    }
}

class Function(val name: String, val url: String, val invocations: Int, val replicas: Int) {
    override fun toString(): String = name
}
