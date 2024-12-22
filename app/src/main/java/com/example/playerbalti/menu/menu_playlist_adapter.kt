package com.example.playerbalti.menu

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuPlaylistBinding
import com.example.playerbalti.storage.db_manager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_playlist_adapter(var name: String,var type:String = "user",var count: Int,var songs: MutableList<Song>,val launchedByPlaylistContent:Boolean = false) : BottomSheetDialogFragment() {
    lateinit var b:MenuPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuPlaylistBinding.inflate(inflater,container,false)

        //set values:
        b.name.text = name
        b.count.text = if(count==1) "$count song" else "$count songs"

        if(type=="system"){
            b.containerDelete.visibility = View.GONE
            b.containerRename.visibility = View.GONE
            b.containerEdit.visibility = View.GONE
            b.line2.visibility = View.GONE
            b.line3.visibility = View.GONE
        }


        b.containerRename.setOnClickListener{
            showCustomDialog()
        }
        b.containerPlayall.setOnClickListener{
            //get songs
            data.playAll(songs)
            //hide menu
            this.dismiss()
        }
        b.containerPlaynext.setOnClickListener{
            //get songs
            data.addNextAll(requireContext(),songs)
            //hide menu
            this.dismiss()
        }
        b.containerAddQueue.setOnClickListener{
            //get songs
            data.addAll(requireContext(),songs)
            //hide menu
            this.dismiss()
        }

        b.containerDelete.setOnClickListener{
            if(count==0){
                for (i in data.songsPlaylists){
                    if(i.name==name){
                        data.songsPlaylists.remove(i)
                    }
                }
                this.dismiss()
            }else{
                val n = db_manager.removePlaylist(requireContext(),name)
                if(n){
                    if(launchedByPlaylistContent){
                        data.playlist_deleted = true
                    }
                    Toast.makeText(requireContext(), "playlist deleted", Toast.LENGTH_SHORT).show()
                }

                this.dismiss()
            }
        }



        //disable features until they are ready
        data.disableContainer(requireContext(),b.containerEdit)
        data.disableContainer(requireContext(),b.containerSave)

        //disable features in some cases
        if(count==0) {
            b.containerPlayall.isEnabled = false
            b.containerPlaynext.isEnabled = false
            b.containerAddQueue.isEnabled = false
        }

        return b.root
    }

    private fun showCustomDialog() {
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.rename_playlist, null)

        // Build the AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        // Create the AlertDialog
        val customDialog: AlertDialog = builder.create()
        // Set up any interactions or functionalities
        val create = dialogView.findViewById<Button>(R.id.rename)
        val editText = dialogView.findViewById<EditText>(R.id.input)
        create.setOnClickListener {
            if (!editText.text.isBlank() && !data.existNamePlaylist(editText.text.toString())) {
                //add this playlist:
                try {
                    //update current name
                    val n= db_manager.rename(requireContext(),name,editText.text.toString())
                    if(n){
                        //save new name in last renamed playlist in data to be used later
                        data.lastRenamedPlaylist = editText.text.toString()
                        Toast.makeText(this.context?.applicationContext, "playlist renamed", Toast.LENGTH_SHORT).show()
                    }

                    // Dismiss the dialog
                    customDialog.dismiss()
                    //dismiss the bottom sheet
                    this.dismiss()

                } catch (e: Exception) {
                    Log.e("error", "error inside create playlist onclick: \n$e")
                }
            }
        }
        // Show the dialog
        customDialog.show()
    }
}
