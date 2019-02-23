package com.example.picar

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.IOException

class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
    private var connectSuccess: Boolean = true
    @SuppressLint("StaticFieldLeak")
    private val context: Context

    init {
        this.context = c
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ControlActivity.m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
    }

    override fun doInBackground(vararg p0: Void?): String? {
        try {
            if (ControlActivity.m_bluetoothSocket == null || !ControlActivity.m_isConnected) {
                ControlActivity.m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = ControlActivity.m_bluetoothAdapter.getRemoteDevice(ControlActivity.m_address)
                ControlActivity.m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(ControlActivity.m_myUUID)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                ControlActivity.m_bluetoothSocket!!.connect()
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
            ControlActivity.m_isConnected = true
        }
        ControlActivity.m_progress.dismiss()
    }
}