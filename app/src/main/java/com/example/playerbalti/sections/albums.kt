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
import com.example.playerbalti.adapters.AlbumsAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.FragmentAlbumsBinding
import com.example.playerbalti.menu.menu_sort_albums_adapter
import com.example.playerbalti.storage.shared

class albums : Fragment() {
    lateinit var b: FragmentAlbumsBinding

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
        b = FragmentAlbumsBinding.inflate(inflater,container,false)
        Log.e("mainActivitymsg", "loading albums")
        //load folders to array:


        //set number of folders
        b.albumCount.text = data.songsAlbum.count().toString() + " albums"

        sort()
        setList()

        b.sort.setOnClickListener{
            val adapter = menu_sort_albums_adapter()
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
        if(shared.albums_sort_type=="name-asc"){
            data.songsAlbum.sortBy { it.name }
            b.sort.text = "Album ↑"
        }else if(shared.albums_sort_type=="name-desc"){
            data.songsAlbum.sortByDescending { it.name }
            b.sort.text = "Album ↓"
        }

        else if(shared.albums_sort_type=="artist-asc"){
            data.songsAlbum.sortBy { it.artist }
            b.sort.text = "Album artist ↑"
        }else if(shared.albums_sort_type=="artist-desc"){
            data.songsAlbum.sortByDescending { it.artist }
            b.sort.text = "Album artist ↓"
        }

        else if(shared.albums_sort_type=="count-asc"){
            data.songsAlbum.sortBy { it.trackCount }
            b.sort.text = "Songs count ↑"
        }else if(shared.albums_sort_type=="count-desc"){
            data.songsAlbum.sortByDescending { it.trackCount }
            b.sort.text = "Songs count ↓"
        }

        else if(shared.albums_sort_type=="year-asc"){
            data.songsAlbum.sortBy { it.year }
            b.sort.text = "Year ↑"
        } else if(shared.albums_sort_type=="year-desc"){
            data.songsAlbum.sortByDescending { it.year }
            b.sort.text = "Year ↓"
        }
    }

    private fun setList() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewAlbum)


        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = AlbumsAdapter(data.songsAlbum,childFragmentManager)
        recyclerView.adapter = adapter
    }

}