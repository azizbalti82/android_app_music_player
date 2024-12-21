package com.example.playerbalti.sections

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.playlistAdapter
import com.example.playerbalti.chooseTracks
import com.example.playerbalti.content.playlistContent
import com.example.playerbalti.data
import com.example.playerbalti.databinding.FragmentPlaylistsBinding
import com.example.playerbalti.playlist
import com.example.playerbalti.storage.db_manager


class playlists : Fragment() {
    lateinit var b: FragmentPlaylistsBinding

    override fun onResume() {
        super.onResume()
        //initialize ui
        loadPlaylists()
        setList()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b = FragmentPlaylistsBinding.inflate(inflater,container,false)

        //load user playlists one time
        loadUserPlaylists()

        //set listeners
        b.add.setOnClickListener {
            showCustomDialog()
        }

        b.favorite.setOnClickListener {
            val intent = Intent(requireContext(), playlistContent::class.java)
            intent.putExtra("name","Favorites")
            intent.putExtra("type","system")
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            requireContext().startActivity(intent)
        }

        b.recentlyPlayed.setOnClickListener {
            val intent = Intent(requireContext(), playlistContent::class.java)
            intent.putExtra("name","RecentlyPlayed")
            intent.putExtra("type","system")
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            requireContext().startActivity(intent)
        }

        b.recentlyAdded.setOnClickListener {
            val intent = Intent(requireContext(), playlistContent::class.java)
            intent.putExtra("name","RecentlyAdded")
            intent.putExtra("type","system")
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            requireContext().startActivity(intent)
        }

        b.mostPlayed.setOnClickListener {
            val intent = Intent(requireContext(), playlistContent::class.java)
            intent.putExtra("name","MostPlayed")
            intent.putExtra("type","system")
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            requireContext().startActivity(intent)
        }

        b.refrech.setOnRefreshListener {
            loadUserPlaylists()
            loadPlaylists()
            setList()
            b.refrech.isRefreshing = false
        }

        return b.root
    }

    fun showCustomDialog() {
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.create_playlist, null)

        // Build the AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        // Create the AlertDialog
        val customDialog: AlertDialog = builder.create()
        // Set the custom background drawable for the AlertDialog
        //customDialog.window?.setBackgroundDrawableResource(R.drawable.create_playlist_shape)

        // Set up any interactions or functionalities
        val create = dialogView.findViewById<Button>(R.id.create)
        var editText = dialogView.findViewById<EditText>(R.id.input)
        create.setOnClickListener {
            if(editText.text.isBlank()){
                Toast.makeText(requireContext(), "write something first", Toast.LENGTH_SHORT).show()
            }else if(data.existNamePlaylist(editText.text.toString())){
                Toast.makeText(requireContext(), "playlist already exist", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    //create empty playlist
                    data.songsPlaylists.add(playlist(editText.text.toString(), 0, ArrayList()))

                    //add songs to playlist with chooseTracks
                    val intent = Intent(requireContext(), chooseTracks::class.java)
                    intent.putExtra("playListName",editText.text.toString())
                    startActivity(intent)

                    //setList()
                    // Dismiss the dialog
                    customDialog.dismiss()
                } catch (e: Exception) {
                    Log.e("error", "error inside create playlist onclick: \n$e")
                }
            }
        }
        // Show the dialog
        customDialog.show()
    }

    fun loadUserPlaylists(){
        data.songsPlaylists.clear()
        //user playlists
        val names = db_manager.getPlaylistsNames(requireContext()) //playlists names
        for (name in names){
            val songs = db_manager.read(requireContext(),name)
            data.songsPlaylists.add(playlist(name, songs.size, songs))
        }
    }
    fun loadPlaylists(){
        //load playlists from database:
        db_manager.read_Favorites(requireContext())
        b.favoriteCount.text = "${data.getSystemPlaylistSongs("Favorites").size} songs"
        db_manager.read_Recent(requireContext())
        b.recentlyPlayedCount.text = "${data.getSystemPlaylistSongs("RecentlyPlayed").size} songs"
        db_manager.read_mostPlayed(requireContext())
        b.mostPlayedCount.text = "${data.getSystemPlaylistSongs("MostPlayed").size} songs"

    }

    fun setList() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewPlaylist)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        //create empty list:

        // Set up the Adapter
        var adapter = playlistAdapter(data.songsPlaylists, requireFragmentManager())
        recyclerView.adapter = adapter
    }
}