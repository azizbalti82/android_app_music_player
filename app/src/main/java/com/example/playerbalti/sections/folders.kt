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
import com.example.playerbalti.adapters.FolderAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.FragmentFoldersBinding
import com.example.playerbalti.menu.menu_sort_folders_adapter
import com.example.playerbalti.storage.shared

class folders : Fragment() {
    lateinit var b: FragmentFoldersBinding

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
        b = FragmentFoldersBinding.inflate(inflater,container,false)

        //set number of folders
        b.foldersCount.text = data.songsDir.count().toString() + " folders"

        //sort
        sort()
        setList()

        b.sort.setOnClickListener{
            val adapter = menu_sort_folders_adapter()
            adapter.show(requireFragmentManager(), adapter.tag)
        }

        b.refrech.setOnRefreshListener {
            sort()
            setList() //set list with new directories
            b.refrech.isRefreshing = false
        }

        return b.root
    }

    fun sort(){
        if(shared.folders_sort_type=="name-asc"){
            data.songsDir.sortBy { it.name }
            b.sort.text = "name ↑"
            Log.d("genres", "sort: name-asc")
        }else if(shared.folders_sort_type=="name-desc"){
            data.songsDir.sortByDescending { it.name }
            b.sort.text = "name ↓"
            Log.d("genres", "sort: name-desc")
        }else if(shared.folders_sort_type=="count-asc"){
            data.songsDir.sortBy { it.trackCount }
            b.sort.text = "Songs count ↑"
            Log.d("genres", "sort: count-asc")
        }else if(shared.folders_sort_type=="count-desc"){
            data.songsDir.sortByDescending { it.trackCount }
            b.sort.text = "Songs count ↓"
            Log.d("genres", "sort: count-desc")
        }
    }

    private fun setList() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewFolder)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // Set up the Adapter
        var adapter = FolderAdapter(data.songsDir, requireFragmentManager())
        recyclerView.adapter = adapter
    }

}