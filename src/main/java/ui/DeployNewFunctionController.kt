package ui

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import tornadofx.Controller
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL

class DeployNewFunctionController : Controller() {
    fun deploy(name: String, filePath: String) {
        // Read the config file found in the resources directory
        val config = File(javaClass.classLoader.getResource("config.json").file)
        val parser = JSONParser()
        val json = parser.parse(FileReader(config)) as JSONObject

        // Get the server properties
        val server = json["server"] as String
        val port = (json["port"] as Long).toInt()

        // Create an HTTP URL connection and set a POST request to the server with all needed information
        val url = URL("http://$server:$port/post")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("func_name", name)
        connection.setRequestProperty("replicas", "1")
        connection.doOutput = true

        // Stream the ZIP file to the output stream of this HTTP connection
        val fis = FileInputStream(filePath)
        val bis = BufferedInputStream(fis)
        connection.outputStream.write(bis.readBytes())

        bis.close()
        fis.close()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            println("Hello World")
        }
    }
}
