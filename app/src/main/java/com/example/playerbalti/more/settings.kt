package com.example.playerbalti.more

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivitySettingsBinding

class settingsActivity : AppCompatActivity() {
    lateinit var b: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)


        //settings:
        b.containerGeneral.setOnClickListener{
            val intent = Intent(this, SettingsGeneralActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerAppearance.setOnClickListener{
            val intent = Intent(this, SettingsAppearanceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerAudio.setOnClickListener{
            val intent = Intent(this, SettingsAudioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerBackup.setOnClickListener{
            val intent = Intent(this, SettingsBackupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerPlayback.setOnClickListener{
            val intent = Intent(this, SettingsPlaybackActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }

        b.cancelButton.setOnClickListener {
            this.finish()
        }

        //data.disableContainer(this, b.containerAudio)
        //data.disableContainer(this, b.containerAppearance)
        data.disableContainer(this, b.containerBackup)
        data.disableContainer(this, b.containerPlayback)
    }
}