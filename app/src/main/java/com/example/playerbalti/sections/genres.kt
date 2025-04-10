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
import com.example.playerbalti.adapters.GenresAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.FragmentGenresBinding
import com.example.playerbalti.menu.menu_sort_genres_adapter
import com.example.playerbalti.storage.shared

class genres : Fragment() {
    lateinit var b: FragmentGenresBinding

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
        b = FragmentGenresBinding.inflate(inflater,container,false)

        //set number of folders
        b.genresCount.text = data.songsGenres.count().toString() + " genres"


        //sort
        sort()
        setList()


        b.sort.setOnClickListener{
            val adapter = menu_sort_genres_adapter()
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
        if(shared.genres_sort_type=="name-asc"){
            data.songsGenres.sortBy { it.name }
            b.sort.text = "name ↑"
            Log.d("genres", "sort: name-asc")
        }else if(shared.genres_sort_type=="name-desc"){
            data.songsGenres.sortByDescending { it.name }
            b.sort.text = "name ↓"
            Log.d("genres", "sort: name-desc")
        }else if(shared.genres_sort_type=="count-asc"){
            data.songsGenres.sortBy { it.count }
            b.sort.text = "Songs count ↑"
            Log.d("genres", "sort: count-asc")
        }else if(shared.genres_sort_type=="count-desc"){
            data.songsGenres.sortByDescending { it.count }
            b.sort.text = "Songs count ↓"
            Log.d("genres", "sort: count-desc")
        }
    }

    private fun setList() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewGenre)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = GenresAdapter(data.songsGenres,requireFragmentManager())
        recyclerView.adapter = adapter
    }
}