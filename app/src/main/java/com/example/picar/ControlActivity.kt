@file:Suppress("DEPRECATION")

package com.example.picar

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.util.*
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import android.widget.CompoundButton
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener






@Suppress("DEPRECATION")
class ControlActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("52d82dc4-3628-4b3e-ae69-a1fec1384e4f")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        lights_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                sendCommand("lights_up")
            }
            else{
                sendCommand("lights_down")
            }

        }


        go_forward.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    textView.text = "jedziesz"
                    sendCommand("go")
                }
                else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    textView.text = "nie jedziesz"
                    sendCommand("stop")
                }
                return false
            }
        })
        go_backward.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    textView.text = "jedziesz do tyłu"
                    sendCommand("go_back")
                }
                else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    textView.text = "nie jedziesz"
                    sendCommand("stop")
                }
                return false
            }
        })

        turn_left_button.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    textView2.text = "skręcasz w lewo"
                    sendCommand("go_back")
                }
                else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    textView2.text = "prosto"
                    sendCommand("left")
                }
                return false
            }
        })

        turn_right_button.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    textView2.text = "skręcasz w prawo"
                    sendCommand("go_back")
                }
                else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    textView2.text = "prosto"
                    sendCommand("right")
                }
                return false
            }
        })

        disconnect_button.setOnClickListener { disconnect() }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        @SuppressLint("StaticFieldLeak")
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }
}