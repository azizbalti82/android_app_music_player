package com.example.playerbalti.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.databinding.ActivityEqualizerBinding

class equalizerActivity : AppCompatActivity() {
    lateinit var b: ActivityEqualizerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEqualizerBinding.inflate(layoutInflater)
        setContentView(b.root)


        b.switch0.setOnCheckedChangeListener { s, isChecked ->
            if(isChecked){
            }else{
            }
        }

        b.cancelButton.setOnClickListener{
            this.finish()
        }
    }
}