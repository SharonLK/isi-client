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
        val file = File(javaClass.classLoader.getResource("config.json").file)

        val parser = JSONParser()
        val json = parser.parse(FileReader(file)) as JSONObject

        val server = json["server"] as String
        val port = (json["port"] as Long).toInt()

        val url = URL("http://$server:$port")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val socketIn = BufferedReader(InputStreamReader(connection.inputStream))

            val sb = StringBuilder()
            var line = socketIn.readLine()
            while (line != null) {
                sb.append(line).append("\n")

                line = socketIn.readLine()
            }

            val data = parser.parse(sb.toString()) as JSONObject

            val functions = data["functions"] as JSONArray
            for (i in 0 until functions.size) {
                val function = functions[i] as JSONObject
                names.add(Function(function["name"] as String, function["url"] as String))
            }

            socketIn.close()
        }
    }

    fun downloadFunction(function: String) {
        println(function)
    }
}

class Function(val name: String, val url: String) {
    override fun toString(): String = name
}
