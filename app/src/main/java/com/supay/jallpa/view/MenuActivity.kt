package com.supay.jallpa.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.main_layout.*




class MenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        map.setOnClickListener {


            val myIntent = Intent(this, MapsActivity::class.java)
            startActivity(myIntent)
        }

        producer.setOnClickListener {
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent) }
    }
}