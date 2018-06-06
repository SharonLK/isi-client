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
    fun deploy(name: String, replicas: String, filePath: String) {
        val config = File(javaClass.classLoader.getResource("config.json").file)

        val parser = JSONParser()
        val json = parser.parse(FileReader(config)) as JSONObject

        val server = json["server"] as String
        val port = (json["port"] as Long).toInt()

        val url = URL("http://$server:$port/post")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("func_name", name)
        connection.setRequestProperty("replicas", replicas)
        connection.doOutput = true

        val file = File(filePath)
        val bytes = ByteArray(file.length().toInt())
        val fis = FileInputStream(filePath)
        val bis = BufferedInputStream(fis)
        bis.read(bytes, 0, bytes.size)
        val os = connection.outputStream
        os.write(bytes, 0, bytes.size)
        os.flush()

        os.close()
        bis.close()
        fis.close()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            println("Hello World")
        }
    }
}
