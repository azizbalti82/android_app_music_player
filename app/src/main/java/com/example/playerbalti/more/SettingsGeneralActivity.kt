package com.example.playerbalti.more

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivitySettingsGeneralBinding
import com.example.playerbalti.menu.menu_sort_genres_adapter
import com.example.playerbalti.menu.menu_tabs_selector_adapter
import com.example.playerbalti.storage.shared

class SettingsGeneralActivity : AppCompatActivity() {
    lateinit var b: ActivitySettingsGeneralBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsGeneralBinding.inflate(layoutInflater)
        setContentView(b.root)


        //setup ui:         ------------------------------------------------------------------------
        b.containerRememberTabCheckbox.isChecked = shared.rememberLastTab

        //------------------------------------------------------------------------------------------
        b.cancelButton.setOnClickListener{
            this.finish()
        }


        b.containerRescan.setOnClickListener {
            data.getAllSongs(this,"Songs updated successfully","Error while updating songs")
        }

        b.containerManageTabs.setOnClickListener {
            val adapter = menu_tabs_selector_adapter()
            adapter.show(supportFragmentManager, adapter.tag)
        }
        b.containerRememberTab.setOnClickListener {
            b.containerRememberTabCheckbox.isChecked = !b.containerRememberTabCheckbox.isChecked
        }
        b.containerRememberTabCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            shared.set(this,"settingsGeneral","rememberLastTab",isChecked)
        }

        data.disableContainer(this, b.containerLanguage,"English only available")
        data.disableContainer(this, b.containerHiddenFolders)
        data.disableContainer(this, b.containerHiddenSongs)
        data.disableContainer(this, b.containerLastAdded)
    }
}