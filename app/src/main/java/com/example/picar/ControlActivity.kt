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
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import android.widget.CompoundButton
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener






@Suppress("DEPRECATION")
open class ControlActivity : AppCompatActivity(), SensorEventListener  {


    lateinit var sensorManager: SensorManager
    override fun onSensorChanged(event: SensorEvent?) {
        val orientations = orientationToDegree(event!!)
        if(rotation_switch.isChecked)
        {

            if(orientations[2] > -60) {
                Car(window,textView2).turn_right()
            } else if(orientations[2] < -105) {
                Car(window,textView2).turn_left()
            } else if(orientations[2] > -100 && orientations[2] < -80 ) {
                Car(window,textView2).go_straight()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )

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
                    Car(window,textView2).turn_left()
                }
                else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    Car(window,textView2).go_straight()
                }
                return false
            }
        })

        turn_right_button.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    Car(window,textView2).turn_right()
                }
                else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    Car(window,textView2).go_straight()
                }
                return false
            }
        })

        disconnect_button.setOnClickListener { disconnect() }
    }

    internal fun sendCommand(input: String) {
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

    private fun orientationToDegree(event : SensorEvent): FloatArray {
        val rotationMatrix = FloatArray(16)
        SensorManager.getRotationMatrixFromVector(
            rotationMatrix, event!!.values
        )
        val remappedRotationMatrix = FloatArray(16)
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            SensorManager.AXIS_X,
            SensorManager.AXIS_Z,
            remappedRotationMatrix
        )

        val orientations = FloatArray(3)
        SensorManager.getOrientation(remappedRotationMatrix, orientations)
        for (i in 0..2) {
            orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
        }
        return orientations
    }


}