package com.mithilakshar.alertmobile

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mithilakshar.alertmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),SensorEventListener {

    lateinit var binding: ActivityMainBinding
    private lateinit var sensormanager:SensorManager
    private var acceleration =0f
    private var accelerationcurrent=0f
    private var accelerationlast=0f
    private var threshold=10.0f
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaPlayer=MediaPlayer.create(this,R.raw.police)
        mediaPlayer.isLooping=true

        sensormanager=getSystemService(SENSOR_SERVICE) as SensorManager
        sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensormanager.registerListener(this,it,SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_UI)
        }


    }

    override fun onSensorChanged(p0: SensorEvent?) {
       val x=p0?.values?.get(0) ?:0f
       val y=p0?.values?.get(1) ?:0f
       val z=p0?.values?.get(2) ?:0f

        accelerationlast=accelerationcurrent
        accelerationcurrent=kotlin.math.sqrt((x*x+y*y+z*z).toDouble()).toFloat()
        val delta=kotlin.math.abs(accelerationcurrent-accelerationlast)
        acceleration=acceleration*0.7f+delta

        if (acceleration>threshold){

            binding.apply {

                text.text="ON"
                text.setTextColor(ActivityCompat.getColor(this@MainActivity,R.color.RED))
                mediaPlayer.start()
                animationView.visibility=View.VISIBLE
                imageView.visibility=View.GONE


            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }
}