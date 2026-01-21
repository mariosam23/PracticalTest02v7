package ro.pub.cs.systems.eim.practicaltest02v7

import android.util.Log

import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class CommunicationThread(
    private val serverThread: ServerThread,
    private val socket: Socket
) : Thread() {
    
    companion object {
        const val TAG = "CommunicationThread"
    }

    override fun run() {
        try {
            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val printWriter = PrintWriter(socket.getOutputStream(), true)

            Log.i(TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!")

            // read the command from the client
            val command = bufferedReader.readLine()
            val clientID = bufferedReader.readLine()

            if (command == null || command.isEmpty()) {
                Log.e(TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!")
                return
            }

            Log.d(TAG, "Command recv from client: $command with client id $clientID")

            var result: String = ""
            if (command.contains("set")) {
                val min = command.split(",")[1]
                val sec = command.split(",")[2]
                serverThread.setData(clientID, "$min,$sec")
                result = "Alarm set!"
            } else if (command.contains("reset")) {
                serverThread.clearData(clientID)
                result = "Alarm reset!"
                Log.d(TAG, "RESEEEEET")
            } else if (command.contains("poll")) {
                val socket = Socket(Constants.URL, 13)
                val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val httpResult = bufferedReader.readLine()
                socket.close()

                // 61061 26-01-21 12:44:24 00 0 0 309.8 UTC(NIST) *
                val time = httpResult.split(" ")[2]
                val min = time.split(":")[1].toInt()
                val sec = time.split(":")[2].toInt()

                val existingTime = serverThread.getData(clientID)
                if (existingTime.isEmpty()) {
                    result = "none"
                } else {
                    val alarmMin = existingTime.split(",")[0].toInt()
                    val alarmSec = existingTime.split(",")[1].toInt()

                    if (min > alarmMin) {
                        result = "inactive"
                    } else if (min < alarmMin) {
                        result = "active"
                    } else {
                        if (sec > alarmSec) {
                            result = "inactive"
                        } else {
                            result = "active"
                        }
                    }
                }

                Log.d(TAG, "POLLLL")
            }

            printWriter.println(result)
            printWriter.flush()
        } catch (ioException: IOException) {
            Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.message)
        } catch (jsonException: JSONException) {
            Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.message)
        } catch (exception: Exception) {
            Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: " + exception.message)
        } finally {
            try {
//                socket.close()
            } catch (ioException: IOException) {
                Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.message)
            }
        }
    }
}
