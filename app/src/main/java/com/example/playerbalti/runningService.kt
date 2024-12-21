package com.example.playerbalti

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class RunningService : Service() {
    private val channelId = "running"

    override fun onCreate() {
        super.onCreate()
        /*
        //initialize mini player -------------------------------------------------------------------------
        db_manager.getAll_queue(this) //now the queue is restored
        data.queueSelectedSong = shared.getInt(this,"player","lastSongIndex",0)
        data.queueSelectedIsPlaying = false
        data.miniPlayerStatue("initialize")
        //------------------------------------------------------------------------------------------------
         */
        start()
    }


    private fun start() {
        Log.d("storage", "service started")
        // Check if SDK is Oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, "default", importance)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification  = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("song title")
                .setContentText("artist")
                .build()
            startForeground(1, notification)
            Log.d("MyService", "Notification created")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
