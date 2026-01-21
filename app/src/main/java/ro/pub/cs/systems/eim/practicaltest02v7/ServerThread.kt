package ro.pub.cs.systems.eim.practicaltest02v7

import android.util.Log
import cz.msebera.android.httpclient.client.ClientProtocolException
import java.io.IOException
import java.net.ServerSocket

class ServerThread(port: Int) : Thread() {

    private var serverSocket: ServerSocket? = initServerSocket(port)
    private val alarmData: HashMap<String, String> = HashMap()

    companion object {
        const val TAG = "ServerThread"
    }
    
    private fun initServerSocket(port: Int): ServerSocket? {
        try {
            serverSocket = ServerSocket(port)
        } catch (e: IOException) {
            Log.e(TAG, "[SERVER THREAD] An exception has occurred: " + e.message)
            return null
        }

        return serverSocket
    }

    override fun run() {
        try {
            if (serverSocket == null) {
                Log.e(TAG, "[SERVER THREAD] Server socket is null!")
                return
            }
            while (!currentThread().isInterrupted) {
                Log.i(TAG, "[SERVER THREAD] Waiting for a client invocation...")
                val socket = serverSocket!!.accept()
                Log.i(TAG, "[SERVER THREAD] A connection request was received from " + socket.inetAddress.hostAddress + ":" + socket.port)

                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (e: ClientProtocolException) {
            Log.e(TAG, "[SERVER THREAD] An exception has occurred: " + e.message)
        } catch (e: IOException) {
            if (!isInterrupted) {
                Log.e(TAG, "[SERVER THREAD] An exception has occurred: " + e.message)
            }
        }
    }

    fun stopThread() {
        interrupt()
        if (serverSocket != null) {
            try {
                serverSocket!!.close()
            } catch (ioException: IOException) {
                Log.e(TAG, "[SERVER THREAD] An exception has occurred: " + ioException.message)
            }
        }
    }

    @Synchronized
    fun setData(clientID: String, alarm: String) {
        alarmData[clientID] = alarm
    }

    @Synchronized
    fun clearData(clientID: String) {
        alarmData[clientID] = ""
    }

    @Synchronized
    fun getData(clientID: String): String {
        return alarmData[clientID] ?: ""
    }
}