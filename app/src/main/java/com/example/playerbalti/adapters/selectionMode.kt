package com.example.playerbalti.adapters

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivitySelectionModeBinding

class selectionMode: AppCompatActivity() {
    lateinit var b: ActivitySelectionModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySelectionModeBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.closeButtonSelected.setOnClickListener {
            this.finish()
        }

        val pos = intent.getIntExtra("pos",0)
        //setup songs list
        setSongs(pos)
    }

    fun setSongs(pos:Int) {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewSongs)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        //for optimization
        recyclerView.setHasFixedSize(true)
        recyclerView.scrollToPosition(pos)

        // Set up the Adapter
        var adapter = SongSelectionModeAdapter(
            this,
            data.songsSelectionMode,
            b.titleSelected,
            b.selectAllButtonSelected,
            b.moreButtonSelected
        )
        try {
            //select the song with position 'pos'
            adapter.selectedSongs.add(data.songsSelectionMode[pos])
            adapter.notifyItemChanged(4)
            b.titleSelected.text = "${adapter.selectedSongs.size} selected"
        }catch (e:Exception){
            Log.e("selectionMode", "setSongs:",e )
        }

        recyclerView.adapter = adapter
    }
}