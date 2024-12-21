package com.example.playerbalti.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.SongAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityGenreContentBinding
import com.example.playerbalti.menu.menu_genre_adapter
import com.example.playerbalti.menu.menu_sort_genres_adapter

class genreContent : AppCompatActivity() {
    lateinit var  b : ActivityGenreContentBinding
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityGenreContentBinding.inflate(layoutInflater)
        setContentView(b.root)
        //show the songs inside a genre//
        //get genre name from parent
        var name = intent.getStringExtra("name")

        //load songs
        if(!name.isNullOrBlank()){
            //set genre name
            b.title.text = name
            //get songs
            var songs = data.getGenreSongs( name)
            count = songs.size

            //get total ms of all songs
            var total:Long = 0
            for(i in songs){
                total+=i.duration
            }
            //set info
            b.info.text = "$count songs • ${data.getDurationString(total)}"

            //set songs
            if(songs.isEmpty()){
                b.empty.visibility = View.VISIBLE
                b.recyclerViewLayout.visibility = View.INVISIBLE
            }else{
                b.empty.visibility = View.INVISIBLE
                b.recyclerViewLayout.visibility = View.VISIBLE

                // Find the RecyclerView in your layout
                var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewSongs)

                // Set up the LinearLayoutManager
                val layoutManager = LinearLayoutManager(this)
                recyclerView.layoutManager = layoutManager

                // Set up the Adapter
                var adapter = SongAdapter(songs,supportFragmentManager)
                recyclerView.adapter = adapter

                //lets add listeners:
                b.play.setOnClickListener{
                    data.playAll(songs)
                }
                b.shuffle.setOnClickListener{
                    //get shuffled new list
                    var songsList = data.shuffle(songs)
                    Log.d("mainActivitymsg","songs shuffled")
                    data.playAll(songsList)
                }
            }
        }

        b.cancelButton.setOnClickListener{
            finish()
        }

        b.option.setOnClickListener{
            val adapter = menu_genre_adapter(b.title.text.toString(),count)
            adapter.show(this.supportFragmentManager, adapter.tag)
        }

        b.sort.setOnClickListener{
            val adapter = menu_sort_genres_adapter()
            adapter.show(supportFragmentManager, adapter.tag)
        }
    }
}