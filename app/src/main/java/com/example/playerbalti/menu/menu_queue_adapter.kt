package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.QueueAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuQueueBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_queue_adapter : BottomSheetDialogFragment() {
    lateinit var b:MenuQueueBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuQueueBinding.inflate(inflater,container,false)

        //set values:
        val count = data.queue.size
        b.count.text = if(count==1) "$count song" else "$count songs"

        //set listeners
        b.close.setOnClickListener {
            this.dismiss()
        }

        b.addToPlaylist.setOnClickListener {
            val adapter = menu_playlist_add_adapter(data.queue)
            adapter.show(requireFragmentManager(), adapter.tag)
            this.dismiss()
        }

        b.clear.setOnClickListener {
            data.stop()
            //clear playing queue
            data.queue.clear()
            //hide mini_player
            data.miniplayer?.visibility = View.INVISIBLE
            //reset related variables
            data.queueSelectedSong = -1
            data.queueSelectedIsPlaying = false

            //closing current page
            this.dismiss()
            //closing parent page (player)
            if(data.player_activity != null){
                data.player_activity!!.finish()
            }

            data.player_activity = null
            Toast.makeText(requireContext(), "queue cleared", Toast.LENGTH_SHORT).show()
        }

        setupRecycler()

        return b.root
    }

    private fun setupRecycler() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewQueue)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // Use scrollToPosition to scroll to the desired position
        recyclerView.scrollToPosition(data.queueSelectedSong)

        // Set up the Adapter
        var adapter = QueueAdapter(data.queue,childFragmentManager,this)
        recyclerView.adapter = adapter
    }
}