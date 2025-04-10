package com.example.playerbalti.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.databinding.ActivityAboutBinding

class aboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var b:ActivityAboutBinding
        super.onCreate(savedInstanceState)
        b = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.cancelButton.setOnClickListener{
            this.finish()
        }
    }
}