// SongAdapter.kt
package com.example.playerbalti.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.data
import com.example.playerbalti.menu.menu_song_adapter


class AlbumSongAdapter(private var songs:List<Song>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<AlbumSongAdapter.AlbumSongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumSongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_albumsongs, parent, false)
        return AlbumSongViewHolder(view)
    }



    override fun onBindViewHolder(holder: AlbumSongViewHolder, position: Int) {
        val song = songs[position]
        holder.title.text = song.title

        var minutes = song.duration/ 1000 / 60
        var seconds = song.duration / 1000 % 60

        if(seconds<10){
            holder.duration.text = "$minutes:0${seconds}"
        }else{
            holder.duration.text = "$minutes:${seconds}"
        }

        holder.number.text = song.tracknumber.toString()


        holder.menu.setOnClickListener{
            val adapter = menu_song_adapter(song)
            adapter.show(fragmentManager, adapter.tag)
        }

        holder.container.setOnClickListener{
            data.playAll(songs.toMutableList(),position)
        }

    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class AlbumSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val number: TextView = itemView.findViewById(R.id.number)
        val menu: ImageButton = itemView.findViewById(R.id.option)
        var container: LinearLayout = itemView.findViewById(R.id.container)
        // Add other views if needed
    }
}
