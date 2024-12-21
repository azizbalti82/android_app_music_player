package com.example.playerbalti.otherActivities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playerbalti.MainActivity
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityPermissionsBinding

class permissions : AppCompatActivity() {
    lateinit var b:ActivityPermissionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(b.root)

        var storage = stateStorage()

        var notification = stateNotification()


        //--------------------------------- if permissions granted -------------------

        if(storage){
            b.button.isEnabled = false
            b.title3.text = b.title3.text.toString() + " ✅"
        }else{
            b.button.isEnabled = true
        }

        if(notification){
            b.button2.isEnabled = false
            b.title4.text = b.title4.text.toString() + " ✅"
        }else{
            b.button2.isEnabled = true
        }



        val Launcher_Storage = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if(stateNotification()){
                    done()
                }else{
                    b.button.isEnabled = false
                    b.title3.text = b.title3.text.toString() + " ✅"
                }
            }
        }

        val Launcher_Notifications = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if(stateStorage()){
                    done()
                }else{
                    b.button2.isEnabled = false
                    b.title4.text = b.title4.text.toString() + " ✅"
                }
            }
        }

        b.button.setOnClickListener{
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                // Your code for devices running Android versions lower than 10
                Launcher_Storage.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                Launcher_Storage.launch(android.Manifest.permission.READ_MEDIA_AUDIO)
            }
        }

        b.button2.setOnClickListener{
            Launcher_Notifications.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun stateNotification(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }else return true

    }

    private fun stateStorage(): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            //for sdk < 10
            return ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            return ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        // Close the app and remove it from the recent apps list
        finishAffinity()
    }

    fun done(){
        // Permission is granted. Continue the action or workflow in your app
        data.getAllSongs(this)
        Toast.makeText(this, "${data.songsCount} songs found", Toast.LENGTH_SHORT).show()

        // Restart the main activity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        // Finish the current activity
        finish()
    }
}