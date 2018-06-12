package ui

import tornadofx.Controller
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.net.HttpURLConnection

class ResubmitFunctionController : Controller() {
    fun resubmit(name: String, filePath: String) {
        val connection = this.connectServer(requestPath = "resubmit",
                requestType = "POST",
                properties = mapOf("name" to name))

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