package ro.pub.cs.systems.eim.practicaltest02v7

import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: Int,
    private val command: String,
    private val clientID: String,
    private val resultCommandTV: TextView
) : Thread() {

    companion object {
        const val TAG = "ClientThread"
    }

    override fun run() {
        try {
            val socket = Socket(address, port)

            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val printWriter = PrintWriter(socket.getOutputStream(), true)

            printWriter.println(command)
            printWriter.println(clientID)

            val commandInfo: StringBuilder = StringBuilder()
            while (true) {
                val line: String = bufferedReader.readLine() ?: break
                commandInfo.append(line).append("\n")
            }

            val finalInformation = commandInfo.toString()
            resultCommandTV.post {
                resultCommandTV.text = finalInformation.ifEmpty { "No data received from server" }
            }

        } catch (e: IOException) {
            Log.e(TAG, "[CLIENT THREAD] An exception has occurred: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "[CLIENT THREAD] An exception has occurred: " + e.message)
            e.printStackTrace()
        }
    }
}