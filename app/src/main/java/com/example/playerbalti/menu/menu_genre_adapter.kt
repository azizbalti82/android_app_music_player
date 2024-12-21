package com.example.playerbalti.menu

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuGenreBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_genre_adapter(var name:String,var count:Int) : BottomSheetDialogFragment() {
    lateinit var b:MenuGenreBinding

    // Initialize ActivityResultLauncher for permission request
    private val launcher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("mainActivitymsg", "Permission granted")
            } else {
                // Permission denied, handle accordingly
                Log.d("mainActivi tymsg", "Permission denied")
                Toast.makeText(requireContext(), "We need permission to delete", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuGenreBinding.inflate(inflater,container,false)

        //set values:
        b.name.text = name
        if(count==1){
            b.count.text = "$count song"
        }else{
            b.count.text = "$count songs"
        }



        b.containerPlayall.setOnClickListener{
            //get songs
            var songs = data.getGenreSongs(name)
            data.playAll(songs)
            //hide menu
            this.dismiss()
        }
        b.containerPlaynext.setOnClickListener{
            //get songs
            var songs = data.getGenreSongs(name)
            data.addNextAll(requireContext(),songs)
            //hide menu
            this.dismiss()
        }
        b.containerAddQueue.setOnClickListener{
            //get songs
            var songs = data.getGenreSongs(name)
            data.addAll(requireContext(),songs)
            //hide menu
            this.dismiss()
        }

        b.containerAddPlaylist.setOnClickListener{
            var songs = data.getGenreSongs(name)
            val adapter = menu_playlist_add_adapter(songs)
            adapter.show(requireFragmentManager(), adapter.tag)
            this.dismiss()
        }
        b.containerDelete.setOnClickListener{
            permissionCheck(requireContext().applicationContext)
            if(ContextCompat.checkSelfPermission(requireContext().applicationContext,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                var songs = data.getGenreSongs(name)
                //data.deleteFile(requireContext(), songs)
            }
        }

        //disable features until they are ready
        data.disableContainer(requireContext(),b.containerDelete)
        return b.root
    }

    fun permissionCheck(c: Context) {
        Log.d("mainActivitymsg", "test for permissions")
        if (ContextCompat.checkSelfPermission(c,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            launcher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}