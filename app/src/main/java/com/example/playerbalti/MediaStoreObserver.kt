package com.example.playerbalti

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log

class MediaStoreObserver(private val c: Context, handler: Handler?) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        Log.d("mainActivitymsg", "observer: MediaStore content changed")
        // Handle the change, update your UI or perform any necessary actions
        data.updateLists(c)
        data.dataChanged = true
    }

    fun startObserving() {
        val contentResolver: ContentResolver = c.contentResolver

        // URI for audio files in the MediaStore
        val mediaStoreAudioUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        // Register the ContentObserver
        contentResolver.registerContentObserver(
            mediaStoreAudioUri,
            true,
            this
        )
    }

    fun stopObserving() {
        val contentResolver: ContentResolver = c.contentResolver
        contentResolver.unregisterContentObserver(this)
    }
}
