package com.example.playerbalti.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.databinding.ActivitySettingsAppearanceBinding
import com.example.playerbalti.menu.menu_background_adapter
import com.example.playerbalti.menu.menu_tabs_selector_adapter

class SettingsAppearanceActivity : AppCompatActivity() {
    lateinit var b: ActivitySettingsAppearanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsAppearanceBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.cancelButton.setOnClickListener{
            this.finish()
        }

        b.containerTheme.setOnClickListener {
            val adapter = menu_background_adapter()
            adapter.show(supportFragmentManager, adapter.tag)
        }

    }
}