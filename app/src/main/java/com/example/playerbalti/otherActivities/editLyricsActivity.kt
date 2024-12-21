package com.example.playerbalti.otherActivities

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.browser.webviewActivity
import com.example.playerbalti.databinding.ActivityEditLyricsBinding

class editLyricsActivity : AppCompatActivity() {
    lateinit var b: ActivityEditLyricsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditLyricsBinding.inflate(layoutInflater)
        setContentView(b.root)

        try {
            //get album name
            var path = intent.getStringExtra("path")
            var artist = intent.getStringExtra("artist")
            var title  = intent.getStringExtra("title")


            b.search.setOnClickListener{
                try{
                    val intent = Intent(this, webviewActivity::class.java)
                    intent.putExtra("title",artist+" - "+title+" lyrics")

                    this.startActivity(intent)
                }catch(e:Exception){
                    Log.e("mainActivitymsg",e.toString())
                }
            }

            b.past.setOnClickListener{
                // Get the ClipboardManager
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                // Check if there is text in the clipboard
                if (clipboardManager.hasPrimaryClip()) {
                    // Get the text from the clipboard
                    val clip = clipboardManager.primaryClip
                    val text = clip?.getItemAt(0)?.text.toString()

                    //set to the inputfield
                    b.textinput.text?.insert(b.textinput.selectionStart, text)
                }
            }

        }catch (e:Exception){
            Log.e("mainActivitymsg", "error in onclick edit lyrics: \n$e", )
        }

        b.cancelButton.setOnClickListener{
            this.finish()
        }
    }
}