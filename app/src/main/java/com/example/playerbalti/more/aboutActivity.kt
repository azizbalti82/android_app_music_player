package com.example.playerbalti.more

import android.content.Intent
import android.net.Uri
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

        b.myLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/aziz-balti/" // Replace with your desired URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        b.myGithub.setOnClickListener {
            val url = "http://www.github.com/azizbalti82" // Replace with your desired URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        b.myMail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Ensure only email apps handle this intent
                putExtra(Intent.EXTRA_EMAIL, arrayOf("azizbalti.dev@gmail.com")) // Your email address
            }
            startActivity(Intent.createChooser(emailIntent, "Send email via:"))

        }
    }
}