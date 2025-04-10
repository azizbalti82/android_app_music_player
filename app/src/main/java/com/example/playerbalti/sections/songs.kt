package com.example.playerbalti.sections

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.SongAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.FragmentSongsBinding
import com.example.playerbalti.menu.menu_sort_songs_adapter
import com.example.playerbalti.storage.shared

class songs : Fragment() {
    lateinit var b: FragmentSongsBinding

    override fun onResume() {
        super.onResume()
        if (data.dataChanged){
            sort()
            setSongs()
            data.dataChanged = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b = FragmentSongsBinding.inflate(inflater, container, false)
        b.songsCount.text = "${data.songsCount} Songs"

        sort()
        setSongs()

        //lets add listeners:
        b.play.setOnClickListener{
            data.playAll(data.songsList)
        }

        b.shuffle.setOnClickListener{
            //get shuffled new list
            var songsList = data.shuffle(data.songsList)
            Log.d("mainActivitymsg","songs shuffled")
            data.playAll(songsList)
        }

        b.sort.setOnClickListener{
            val adapter = menu_sort_songs_adapter()
            adapter.show(requireFragmentManager(), adapter.tag)
        }

        b.refrech.setOnRefreshListener {
            //initialize ui
            sort()
            setSongs()
            b.refrech.isRefreshing = false
        }

        return b.root
    }


    fun sort(){
        if(shared.songs_sort_type=="name-asc"){
            data.songsList.sortBy { it.title }
            b.sort.text = "Title ↑"
        }else if(shared.songs_sort_type=="name-desc"){
            data.songsList.sortByDescending { it.title }
            b.sort.text = "Title ↓"
        }

        else if(shared.songs_sort_type=="album-asc"){
            data.songsList.sortBy { it.album }
            b.sort.text = "Album ↑"
        }else if(shared.songs_sort_type=="album-desc"){
            data.songsList.sortByDescending { it.album }
            b.sort.text = "Album ↓"
        }

        else if(shared.songs_sort_type=="artist-asc"){
            data.songsList.sortBy { it.artist }
            b.sort.text = "Artist ↑"
        }else if(shared.songs_sort_type=="artist-desc"){
            data.songsList.sortByDescending { it.artist }
            b.sort.text = "Artist ↓"
        }

        else if(shared.songs_sort_type=="duration-asc"){
            data.songsList.sortBy { it.duration }
            b.sort.text = "Duration ↑"
        } else if(shared.songs_sort_type=="duration-desc"){
            data.songsList.sortByDescending { it.duration }
            b.sort.text = "Duration ↓"
        }

        else if(shared.songs_sort_type=="added-asc"){
            data.songsList.sortBy { it.dateAdded }
            b.sort.text = "Date added ↑"
        } else if(shared.songs_sort_type=="added-desc"){
            data.songsList.sortByDescending { it.dateAdded }
            b.sort.text = "Date added ↓"
        }

        else if(shared.songs_sort_type=="modified-asc"){
            data.songsList.sortBy { it.dateModified }
            b.sort.text = "Date modified ↑"
        } else if(shared.songs_sort_type=="modified-desc"){
            data.songsList.sortByDescending { it.dateModified }
            b.sort.text = "Date modified ↓"
        }

        else if(shared.songs_sort_type=="random"){
            data.songsList = data.shuffle(data.songsList)
            b.sort.text = "Random"
        }
    }


    private fun setSongs() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerView)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        //for optimization
        recyclerView.setHasFixedSize(true)

        // Set up the Adapter
        var adapter = SongAdapter(data.songsList,childFragmentManager)
        recyclerView.adapter = adapter
    }
}
