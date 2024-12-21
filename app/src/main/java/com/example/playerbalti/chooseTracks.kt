package com.example.playerbalti

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.adapters.SongsPickAdapter
import com.example.playerbalti.databinding.ActivityChooseTracksBinding


class chooseTracks : AppCompatActivity() {
    lateinit var  b : ActivityChooseTracksBinding

    var name = ""
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityChooseTracksBinding.inflate(layoutInflater)
        setContentView(b.root)

        name = intent.getStringExtra("playListName").toString()

        b.cancelButton.setOnClickListener {
            this.finish()
        }

        //set songs
        setSongs()

        b.refrech.setOnRefreshListener {
            //initialize ui

            //set songs
            setSongs()
            b.refrech.isRefreshing = false
        }

        //search
        b.search.addTextChangedListener {
            val text = b.search.text.toString()
            if(text.isNotEmpty()){
                val songs: MutableList<Song> = ArrayList()
                for(i in data.songsList){
                    if(i.title.contains(text,true)){
                        songs.add(i)
                    }
                }
                setSongs(songs)
            }else{
                setSongs()
            }

            b.done.setOnClickListener{
                this.finish()
            }

        }
    }


     private fun setSongs(songs:MutableList<Song> = data.songsList) {
        // Find the RecyclerView in your layout
        val recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewSongs)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        //for optimization
        recyclerView.setHasFixedSize(true)


        // Set up the Adapter
        val adapter = SongsPickAdapter(songs,this,b.done,this.name)
        recyclerView.adapter = adapter
    }


}