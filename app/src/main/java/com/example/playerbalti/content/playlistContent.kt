package com.example.playerbalti.content

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.adapters.SongAdapter
import com.example.playerbalti.chooseTracks
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityPlaylistContentBinding
import com.example.playerbalti.menu.menu_playlist_adapter
import com.example.playerbalti.playlist
import com.example.playerbalti.storage.db_manager

class playlistContent : AppCompatActivity() {
    lateinit var  b : ActivityPlaylistContentBinding
    var count = 0
    var name = ""
    lateinit var type:String

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if(hasFocus && data.lastRenamedPlaylist.isNotEmpty()){
            name = data.lastRenamedPlaylist
            b.title.text = name
            data.lastRenamedPlaylist = ""
        }

    }

    override fun onStop() {
        super.onStop()
        data.lastRenamedPlaylist = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPlaylistContentBinding.inflate(layoutInflater)
        setContentView(b.root)

        name = intent.getStringExtra("name") ?: ""
        type = intent.getStringExtra("type") ?: "user"//"system" playlist or "user" playlist(by default: user)

        //load songs -------------------------------------------------------------------------------
        var songs:MutableList<Song>
        if(name=="RecentlyAdded") {
            songs = data.getLastAdded()
        }else{
            songs = getSongs(type, name ?: "")
        }

        //setup ui ---------------------------------------------------------------------------------
        if(type=="system"){
            //first of all, lets hide the "add songs" button , because songs will be added automatically
            b.add.visibility = View.GONE

            if(name=="MostPlayed"){
                b.title.text = "Most Played"
            }else if(name=="RecentlyPlayed"){
                b.title.text = "Recently Played"
            } else if(name=="RecentlyAdded"){
                b.title.text = "Recently Added"
            }else{
                b.title.text = "Favorites"
            }
        }else{
            b.title.text = name
        }
        count = songs.size //set songs count

        //set songs --------------------------------------------------------------------------------
        loadPlaylists()
        setSongs(songs)

        //set listeners ----------------------------------------------------------------------------
        b.cancelButton.setOnClickListener{
            finish()
        }

        b.option.setOnClickListener{
            val adapter = menu_playlist_adapter(name,type,count,songs,true)
            adapter.show(this.supportFragmentManager, adapter.tag)
        }

        b.add.setOnClickListener {
            overridePendingTransition(0, 0)// Disable transition animations
            val intent = Intent(this, chooseTracks::class.java)
            intent.putExtra("playListName",name)
            startActivity(intent)
        }


        b.refrech.setOnRefreshListener {
            //get playlists from database
            loadPlaylists()
            loadUserPlaylists()
            //get playlists
            var songs:MutableList<Song>
            if(name=="RecentlyAdded") {
                songs = data.getLastAdded()
            }else{
                songs = getSongs(type, name ?: "")
            }

            setSongs(songs) //set new songs to recycler view
            b.refrech.isRefreshing = false
        }
    }


    fun loadPlaylists(){
        //load playlists from database:
        db_manager.read_Favorites(this)
        db_manager.read_Recent(this)
        db_manager.read_mostPlayed(this)
    }

    fun getSongs(type:String,name:String): MutableList<Song> {
        if(type=="system"){
            return data.getSystemPlaylistSongs(name)
        }else{
            return data.getPlaylistSongs(name)
        }
    }
    fun setSongs(songs:MutableList<Song>){
        if(songs.isEmpty()){
            b.empty.visibility = View.VISIBLE
            b.recyclerViewLayout.visibility = View.INVISIBLE
        }
        else{
            //update ui
            b.empty.visibility = View.INVISIBLE
            b.recyclerViewLayout.visibility = View.VISIBLE

            // Find the RecyclerView in your layout
            var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewSongs)

            // Set up the LinearLayoutManager
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            // Set up the Adapter

            val adapter = SongAdapter(songs,supportFragmentManager)
            recyclerView.adapter = adapter

            //lets add listeners:
            b.play.setOnClickListener{
                data.playAll(songs)
            }
            b.shuffle.setOnClickListener{
                //get shuffled new list
                var songsList = data.shuffle(songs)
                data.playAll(songsList)
            }

        }
    }

    fun loadUserPlaylists(){
        data.songsPlaylists.clear()
        //user playlists
        val names = db_manager.getPlaylistsNames(this) //playlists names
        for (name in names){
            val songs = db_manager.read(this,name)
            data.songsPlaylists.add(playlist(name, songs.size, songs))
        }
    }

}