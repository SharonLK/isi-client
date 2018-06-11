package ui

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL

fun postConnectToServer(propertyMap : Map<String, String> = mapOf(), typeOfRequest : String): HttpURLConnection {
    // Read the config file found in the resources directory
    val config = File(Screen::class.java.getResource("config.json").file)
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