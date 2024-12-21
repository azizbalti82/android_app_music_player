package com.example.playerbalti.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.ArtistAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.FragmentArtistsBinding
import com.example.playerbalti.menu.menu_sort_artists_adapter
import com.example.playerbalti.storage.shared

class artists : Fragment() {
    lateinit var b: FragmentArtistsBinding

    override fun onResume() {
        super.onResume()

        if (data.dataChanged){
            sort()
            setList()
            data.dataChanged = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b = FragmentArtistsBinding.inflate(inflater,container,false)

        //set number of folders
        b.artistCount.text = data.songsArtist.count().toString() + " artist"

        sort()
        setList()

        b.sort.setOnClickListener{
            val adapter = menu_sort_artists_adapter()
            adapter.show(requireFragmentManager(), adapter.tag)
        }

        b.refrech.setOnRefreshListener {
            //initialize ui
            sort()
            setList()
            b.refrech.isRefreshing = false
        }

        return b.root
    }

    fun sort(){
        if(shared.artists_sort_type=="name-asc"){
            data.songsArtist.sortBy { it.name }
            b.sort.text = "Artist ↑"
        }else if(shared.artists_sort_type=="name-desc"){
            data.songsArtist.sortByDescending { it.name }
            b.sort.text = "Artist ↓"
        }

        else if(shared.artists_sort_type=="songs-count-asc"){
            data.songsArtist.sortBy { it.countSongs }
            b.sort.text = "Songs count ↑"
        }else if(shared.artists_sort_type=="songs-count-desc"){
            data.songsArtist.sortByDescending { it.countSongs }
            b.sort.text = "Songs count ↓"
        }

        else if(shared.artists_sort_type=="albums-count-asc"){
            data.songsArtist.sortBy { it.countAlbum }
            b.sort.text = "Albums count ↑"
        }else if(shared.artists_sort_type=="albums-count-desc"){
            data.songsArtist.sortByDescending { it.countAlbum }
            b.sort.text = "Albums count ↓"
        }
    }

    private fun setList() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewArtist)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = ArtistAdapter(data.songsArtist,requireFragmentManager())
        recyclerView.adapter = adapter
    }
}