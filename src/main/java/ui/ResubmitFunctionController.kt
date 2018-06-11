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

class ResubmitFunctionController : Controller() {
    fun resubmit(name: String, filePath: String) {
        val nameProp = mutableMapOf("name" to name)
        val connection = this.postConnectToServer(propertyMap = nameProp, typeOfRequest = "post")

        // Stream the ZIP file to the output stream of this HTTP connection
        val fis = FileInputStream(filePath)
        val bis = BufferedInputStream(fis)
        connection.outputStream.write(bis.readBytes())

        bis.close()
        fis.close()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            println("Function re-submitted successfully")
        }
    }
}