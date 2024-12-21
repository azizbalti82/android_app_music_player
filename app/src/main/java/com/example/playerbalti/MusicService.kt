package com.example.playerbalti
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.browse.MediaBrowser
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.service.media.MediaBrowserService
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle


class MusicService : MediaBrowserService() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()

        // Notification Channel setup for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "music_service_channel"
            val channelName = "Music Player Service"
            var description = "Music playback controls"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                description = description
            }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Initialize MediaSessionCompat
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    // Start playback logic
                }

                override fun onPause() {
                    // Pause playback logic
                }

                override fun onSkipToNext() {
                    // Skip to next song
                }

                override fun onSkipToPrevious() {
                    // Skip to previous song
                }

                override fun onStop() {
                    // Stop playback
                }
            })
            isActive = true
        }

        // Step 3: Create PendingIntent to open MainActivity when notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE )

        // Step 4: Notification Builder setup with MediaStyle and PendingIntent
        val playPauseAction = NotificationCompat.Action(
            R.drawable.play, // Change with actual icon
            "Play/Pause",
            null // Add the pending intent for play/pause here
        )

        notificationBuilder = NotificationCompat.Builder(this, "music_service_channel")
            .setContentTitle("Title")
            .setContentText("Artist")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(playPauseAction)
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setContentIntent(pendingIntent) // Set the PendingIntent to open MainActivity on click

        // Step 5: Start the service in the foreground
        startForeground(1, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // We are not binding to any activity
    }

    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(p0: String, p1: Result<MutableList<MediaBrowser.MediaItem>>) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release() // Release the MediaSession when service is destroyed
    }
}
