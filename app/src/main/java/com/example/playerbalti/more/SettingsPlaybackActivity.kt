package com.example.playerbalti.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.databinding.ActivitySettingsPlaybackBinding

class SettingsPlaybackActivity : AppCompatActivity() {
    lateinit var b: ActivitySettingsPlaybackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsPlaybackBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.cancelButton.setOnClickListener{
            this.finish()
        }
    }
}