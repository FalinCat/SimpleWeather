package com.example.falin.simpleweather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.falin.simpleweather.Controller.LocationController

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val location = LocationController(this)
    }
}
