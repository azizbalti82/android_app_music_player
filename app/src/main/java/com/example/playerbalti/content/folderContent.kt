package com.example.playerbalti.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.adapters.SongAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityFolderContentBinding
import com.example.playerbalti.menu.menu_folder_adapter
import com.example.playerbalti.menu.menu_sort_folders_adapter

class folderContent : AppCompatActivity() {
    lateinit var  b : ActivityFolderContentBinding
    var count = 0
    lateinit var path:String

    override fun onResume() {
        super.onResume()
        loadSongs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFolderContentBinding.inflate(layoutInflater)
        setContentView(b.root)

        //get the path from parent
        path = intent.getStringExtra("path") ?: ""
        b.title.text = path.substringAfterLast('/')

        //load songs
        loadSongs()

        b.cancelButton.setOnClickListener{
            finish()
        }

        b.option.setOnClickListener{
            val adapter = menu_folder_adapter(b.title.text.toString(),count,path.toString())
            adapter.show(this.supportFragmentManager, adapter.tag)
        }

        b.sort.setOnClickListener{
            val adapter = menu_sort_folders_adapter()
            adapter.show(supportFragmentManager, adapter.tag)
        }

        b.refrech.setOnRefreshListener {
            loadSongs()
            b.refrech.isRefreshing = false
        }
    }

    fun loadSongs(){
        if(path != null){
            this.path = path
            var songs:MutableList<Song> = ArrayList()
            //get songs
            for(i in data.songsDir){
                if(i.path == path){
                    songs = i.songs
                    break
                }
            }
            path = path.toString()
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
    }
}