package ui

import tornadofx.Controller
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.net.HttpURLConnection

class DeployNewFunctionController : Controller() {
    fun deploy(name: String, filePath: String) {
        val connection = connectServer(requestPath = "post",
                requestType = "POST",
                properties = mapOf("name" to name))

        // Stream the ZIP file to the output stream of this HTTP connection
        val fis = FileInputStream(filePath)
        val bis = BufferedInputStream(fis)
        connection.outputStream.write(bis.readBytes())

        bis.close()
        fis.close()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            println("Function deployed successfully")
        }
    }
}
