package com.example.picar

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.TextView
import kotlinx.android.synthetic.main.control_layout.*

class Car(window : Window, textView: TextView) : ControlActivity(){
    val windo : Window
    val text : TextView
    init {
        windo = window
        text = textView
    }
    internal fun turn_left(){
        windo.decorView.setBackgroundColor(Color.YELLOW)
        text.text = "skręcasz w lewo"
        sendCommand("left")
    }

    internal fun turn_right(){
        windo.decorView.setBackgroundColor(Color.BLUE)
        text.text = "skręcasz w prawo"
        sendCommand("right")
    }

    internal fun go_straight(){
        windo.decorView.setBackgroundColor(Color.WHITE)
        text.text = "prosto"
        sendCommand("straight")
    }
}