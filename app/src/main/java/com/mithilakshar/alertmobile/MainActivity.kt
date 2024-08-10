package com.mithilakshar.alertmobile

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
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
    private var threshold=9.9f
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
        setVolumeToMax(this)
        mediaPlayer=MediaPlayer.create(this,R.raw.murder)
        mediaPlayer.isLooping=true

        sensormanager=getSystemService(SENSOR_SERVICE) as SensorManager
        sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensormanager.registerListener(this,it,SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_UI)
        }


    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values.getOrNull(0) ?: 0f
            val y = it.values.getOrNull(1) ?: 0f
            val z = it.values.getOrNull(2) ?: 0f

            // Calculate current acceleration
            accelerationlast = accelerationcurrent
            accelerationcurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Calculate the delta change in acceleration
            val delta = kotlin.math.abs(accelerationcurrent - accelerationlast)

            // Increase sensitivity by reducing the smoothing factor
            acceleration = acceleration * 0.95f + delta // Lower factor for higher sensitivity

            // Check if the change exceeds the threshold
            if (acceleration > threshold) {
                binding.apply {
                    text.text = "ON"
                    text.setTextColor(ActivityCompat.getColor(this@MainActivity, R.color.RED))
                    mediaPlayer.start()
                    imageView.visibility = View.GONE
                }
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    fun setVolumeToMax(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // Get the maximum volume level
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        // Set the volume to the maximum level
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
    }
}