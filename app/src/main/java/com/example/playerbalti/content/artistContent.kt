package com.example.playerbalti.content

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.adapters.AlbumsAdapterS
import com.example.playerbalti.adapters.SongAdapter
import com.example.playerbalti.album
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityArtistContentBinding
import com.example.playerbalti.menu.menu_artist_adapter

class artistContent : AppCompatActivity() {
    lateinit var  b : ActivityArtistContentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityArtistContentBinding.inflate(layoutInflater)
        setContentView(b.root)

        //show the songs inside a folder//
        try {
            //get album name
            var name = intent.getStringExtra("artist")
            var songCount = intent.getIntExtra("songCount",0)
            var albumCount = intent.getIntExtra("albumCount",0)


            //set text
            b.artistName.text = name
            b.count.text = "$albumCount Albums • $songCount Songs"

            val artist = data.getArtist(name.toString())
            //get songs
            var songs = artist?.songs
            //get albums
            var albums = artist?.albums


            //set songs & albums
            if (songs.isNullOrEmpty()) {
                b.recyclerViewLayoutSongs.visibility = View.INVISIBLE
                Toast.makeText(this, "Error: unable to load songs", Toast.LENGTH_SHORT).show()
            } else{
                b.recyclerViewLayoutSongs.visibility = View.VISIBLE
                setupSongsRecycler(songs)
            }

            if (albums.isNullOrEmpty()) {
                b.recyclerViewLayoutAlbums.visibility = View.INVISIBLE
                Toast.makeText(this, "Error: unable to load albums", Toast.LENGTH_SHORT).show()
            }else{
                b.recyclerViewLayoutAlbums.visibility = View.VISIBLE
                setupAlbumsRecycler(albums)
            }

            //load artist image
            if(!songs.isNullOrEmpty()) {
                loadImage(songs)
            }


            //lets add listeners:
            b.play.setOnClickListener{
                if(!songs.isNullOrEmpty()) {
                    data.playAll(songs)
                }
            }
            b.shuffle.setOnClickListener{
                if(!songs.isNullOrEmpty()) {
                    //get shuffled new list
                    var songsList = data.shuffle(songs)
                    Log.d("mainActivitymsg", "songs shuffled")
                    data.playAll(songsList)
                }
            }
            b.cancelButton.setOnClickListener {
                finish()
            }
            b.option.setOnClickListener {
                val adapter = menu_artist_adapter(name.toString(),songCount)
                adapter.show(supportFragmentManager, adapter.tag)
            }

        }catch (e:Exception){
            Log.e("mainActivitymsg", "error while loading albumContent")
        }
    }

    private fun setupSongsRecycler(songs: MutableList<Song>) {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewArtistSongs)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = SongAdapter(songs,supportFragmentManager)
        recyclerView.adapter = adapter
    }

    private fun setupAlbumsRecycler(albums: MutableList<album>) {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewArtistAlbums)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = AlbumsAdapterS(albums,supportFragmentManager)
        recyclerView.adapter = adapter
    }

    private fun getPlaybackText(): String {
        var result = "0 Minutes Playback"
        //do it later
        return result
    }

    private fun loadImage(songs:MutableList<Song>){
        //load image
        var url:ByteArray? = null

        for(i in songs){
            if(!i.path.isNullOrBlank()){
                url = data.extractAlbumArt(i.path)
                break
            }
        }

        data.loadImage(this,url,b.img,R.drawable.artist)
    }

}