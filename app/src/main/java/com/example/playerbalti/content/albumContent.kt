package com.example.playerbalti.content

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.AlbumSongAdapter
import com.example.playerbalti.artist
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityAlbumContentBinding
import com.example.playerbalti.menu.menu_album_adapter

class albumContent : AppCompatActivity() {
    lateinit var  b : ActivityAlbumContentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityAlbumContentBinding.inflate(layoutInflater)
        setContentView(b.root)

        //show the songs inside a folder//

        try {
            //get album name
            var name = intent.getStringExtra("name").toString()
            var artist = intent.getStringExtra("artist").toString()
            var year = intent.getIntExtra("year",0)

            b.albumArtist.text = artist
            b.albumName.text = name
            b.year.text = if(year==0) "#" else year.toString()

            //get songs
            var songs = data.getAlbum(name.toString(),artist.toString())?.songs

            //set songs
            if (songs.isNullOrEmpty()) {
                Toast.makeText(this, "Error: unable to load songs", Toast.LENGTH_SHORT).show()
                b.recyclerViewLayout.visibility = View.INVISIBLE
            } else if (songs != null) {
                b.recyclerViewLayout.visibility = View.VISIBLE

                //load image
                var url:ByteArray? = null
                for(i in songs){
                    if(!i.path.isNullOrBlank()){
                        url = data.extractAlbumArt(i.path)
                        break
                    }
                }
                data.loadImage(this,url,b.img,R.drawable.album)

                // Find the RecyclerView in your layout
                var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewAlbumSongs)

                // Set up the LinearLayoutManager
                val layoutManager = LinearLayoutManager(this)
                recyclerView.layoutManager = layoutManager

                // Set up the Adapter
                var adapter = AlbumSongAdapter(songs,supportFragmentManager)
                recyclerView.adapter = adapter
            }

            //lets add listeners:
            b.play.setOnClickListener{
                if (songs != null) {
                    data.playAll(songs)
                }
            }
            b.shuffle.setOnClickListener{
                if (songs != null) {
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
                if (songs != null) {
                    val adapter = menu_album_adapter(name.toString(), songs.size, artist.toString())
                    adapter.show(supportFragmentManager, adapter.tag)
                }
            }

            b.albumArtist.setOnClickListener {
                //search

                if(name.isEmpty() || artist.isEmpty()) {
                    Toast.makeText(this, "invalid artist", Toast.LENGTH_SHORT).show()
                }else{
                    var result: artist? = data.getArtist(artist)

                    if(result != null) {
                        //open artist page
                        try {
                            val intent = Intent(this, artistContent::class.java)
                            intent.putExtra("artist",result.name)
                            intent.putExtra("songCount",result.countSongs)
                            intent.putExtra("albumCount",result.countAlbum)

                            startActivity(intent)

                        } catch (e: Exception) {
                            Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e",)
                        }
                    }
                }
            }
        }catch (e:Exception){
            Log.e("mainActivitymsg", "error while loading albumContent")
        }
    }
}