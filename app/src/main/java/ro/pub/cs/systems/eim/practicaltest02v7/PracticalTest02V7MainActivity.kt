package ro.pub.cs.systems.eim.practicaltest02v7

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PracticalTest02V7MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MAIN"
    }
    private var serverThread: ServerThread? = null
    private var clientThread: ClientThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v7_main)


        val serverPort = findViewById<EditText>(R.id.server_port_edit_text)
        val connectButton = findViewById<Button>(R.id.connect_button)

        val address = findViewById<EditText>(R.id.client_address_edit_text)
        val clientPort = findViewById<EditText>(R.id.client_port_edit_text)
        val command = findViewById<EditText>(R.id.command_edit_text)
        val sendCommandBtn = findViewById<Button>(R.id.send_command_button)
        val resultCommandTV = findViewById<TextView>(R.id.result_command_text_view)

        connectButton.setOnClickListener {
            val port = serverPort.text.toString().toInt()
            if (serverThread == null || !serverThread!!.isAlive) {
                serverThread = ServerThread(port)
                serverThread!!.start()
                Log.d(TAG, "Server started on port $port")
            }
        }

        sendCommandBtn.setOnClickListener {
            val port = clientPort.text.toString().toInt()
            val clientAddress = address.text.toString()
            val command = command.text.toString()

            if (clientThread == null || !clientThread!!.isAlive) {
                clientThread = ClientThread(
                    clientAddress,
                    port,
                    command,
                    "Client",
                    resultCommandTV
                )
                clientThread!!.start()
                Log.d(TAG, "Client started on port $port")
            }
        }
    }

    override fun onDestroy() {
        if (serverThread!!.isAlive) {
            serverThread!!.stopThread()
        }
        super.onDestroy()
    }
}