package com.example.playerbalti.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.databinding.ActivitySettingsBackupBinding

class SettingsBackupActivity : AppCompatActivity() {
    lateinit var b: ActivitySettingsBackupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBackupBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.cancelButton.setOnClickListener{
            this.finish()
        }
    }
}