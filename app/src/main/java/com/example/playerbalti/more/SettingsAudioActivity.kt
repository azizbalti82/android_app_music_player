package com.example.playerbalti.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivitySettingsAudioBinding
import com.example.playerbalti.storage.shared

class SettingsAudioActivity : AppCompatActivity() {
    lateinit var b: ActivitySettingsAudioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsAudioBinding.inflate(layoutInflater)
        setContentView(b.root)

        //set ui -------------------------------------------------------------------
        b.containerShortCheckbox.isChecked = shared.allow_30_sec
        b.containerPauseMuteCheckbox.isChecked = shared.pause_when_mute
        b.containerLowerNotificationCheckbox.isChecked = shared.lower_volume


        //listeners ----------------------------------------------------------------
        b.cancelButton.setOnClickListener{
            this.finish()
        }
        b.containerShort.setOnClickListener {
            b.containerShortCheckbox.isChecked = !b.containerShortCheckbox.isChecked
        }
        b.containerPauseMute.setOnClickListener {
            b.containerPauseMuteCheckbox.isChecked = !b.containerPauseMuteCheckbox.isChecked
        }
        b.containerLowerNotification.setOnClickListener {
            b.containerLowerNotificationCheckbox.isChecked = !b.containerLowerNotificationCheckbox.isChecked
        }


        b.containerShortCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            shared.set(this,"settingsAudio","allow_30_sec",isChecked)
            //refresh songs list
            data.getAllSongs(this)
        }
        b.containerPauseMuteCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            shared.set(this,"settingsAudio","pause_when_mute",isChecked)
        }
        b.containerLowerNotificationCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            shared.set(this,"settingsAudio","lower_volume",isChecked)
        }


        //disable some options until they work properly
        b.containerLowerNotificationCheckbox.isEnabled = false
        b.containerLowerNotification.isEnabled = false
        b.containerPauseMute.isEnabled = false
        b.containerPauseMuteCheckbox.isEnabled = false
    }
}