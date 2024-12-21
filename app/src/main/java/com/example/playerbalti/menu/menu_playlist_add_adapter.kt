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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.adapters.playlistSelectAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuAddPlaylistBinding
import com.example.playerbalti.playlist
import com.example.playerbalti.storage.db_manager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class menu_playlist_add_adapter(var songs: List<Song>) : BottomSheetDialogFragment() {
    lateinit var b:MenuAddPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuAddPlaylistBinding.inflate(inflater,container,false)

        //show existing playlists
        resetAdapter()

        //to create a new playlist
        b.add.setOnClickListener {
            showCustomDialog(songs.toMutableList())
        }

        return b.root
    }

    private fun showCustomDialog(songs:MutableList<Song>) {
        if(!songs.isEmpty()) {
            val inflater = layoutInflater
            val dialogView: View = inflater.inflate(R.layout.create_playlist, null)

            // Build the AlertDialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)

            // Create the AlertDialog
            val customDialog: AlertDialog = builder.create()
            // Set the custom background drawable for the AlertDialog

            // Set up any interactions or functionalities
            val create = dialogView.findViewById<Button>(R.id.create)
            var editText = dialogView.findViewById<EditText>(R.id.input)
            create.setOnClickListener {
                if (!editText.text.isBlank() && !data.existNamePlaylist(editText.text.toString())) {
                    //add this playlist:
                    try {
                        CoroutineScope(Dispatchers.Main).launch {
                            //add to playlist
                            data.songsPlaylists.add(playlist(editText.text.toString(), songs.size, songs))
                            //add to playlist (in database)
                            for (i in songs) {
                                db_manager.addSong(requireContext(), i.id, editText.text.toString())
                            }
                        }


                            resetAdapter()
                            // Dismiss the dialog
                            customDialog.dismiss()


                            //dismiss the bottom sheet
                            this.dismiss()
                            Toast.makeText(
                                this.context?.applicationContext,
                                "added to playlist",
                                Toast.LENGTH_SHORT
                            ).show()


                    } catch (e: Exception) {
                        Log.e("error", "error inside create playlist onclick: \n$e")
                    }
                }
            }
            // Show the dialog
            customDialog.show()
        }
    }
    private fun resetAdapter() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewPlaylist)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = playlistSelectAdapter(data.songsPlaylists,songs,requireFragmentManager(),this)
        recyclerView.adapter = adapter
    }
}