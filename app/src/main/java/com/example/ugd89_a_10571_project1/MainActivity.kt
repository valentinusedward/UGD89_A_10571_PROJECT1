package com.example.ugd89_a_10571_project1

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.hardware.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast




class MainActivity : AppCompatActivity(),SensorEventListener  {
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101

    lateinit var proximitySensor : Sensor
    lateinit var sensorManager : SensorManager

    private var mCamera: Camera? = null
    private var currentCameraId : Int = Camera.CameraInfo.CAMERA_FACING_BACK
    private var mCameraView: CameraView? = null

    var proximitySensorEventListener: SensorEventListener?= object : SensorEventListener{
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
        override fun onSensorChanged(event: SensorEvent) {
            if (event.values[0] == 0f) {
                if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    } else {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    if (mCameraView != null) {
                        mCamera?.stopPreview();
                    }
                    mCamera?.release();
                    try {
                        mCamera = Camera.open(currentCameraId)
                    } catch (e: Exception) {
                        Log.d("Error", "Failed to get Camera" + e.message)
                    }
                    if (mCamera != null) {
                        mCameraView = CameraView(applicationContext, mCamera!!)
                        val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camera_view.addView(mCameraView)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpSensorStuff()
        createNotificationChannel()
        try{
            mCamera = Camera.open()
        }
        catch (e:Exception){
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if(mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {

            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{view : View? -> System.exit(0)}
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val channel1 = NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Modul89_A_10571_PROJECT2")
            .setContentText("Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 ")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            val sides = event.values[0]

            val upDown = event.values[1]

            val color = if (upDown.toInt() == 0 && sides.toInt() == 0)
                Color.GREEN else Color.RED
            if(upDown.toInt() > 5 || sides.toInt() > 5){
                sendNotification()
            }
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
                accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

    }
}