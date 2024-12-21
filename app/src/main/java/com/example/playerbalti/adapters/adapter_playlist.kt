// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.content.playlistContent
import com.example.playerbalti.menu.menu_genre_adapter
import com.example.playerbalti.menu.menu_playlist_adapter
import com.example.playerbalti.playlist


class playlistAdapter(private var playlists:List<playlist>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<playlistAdapter.playlistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): playlistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist, parent, false)
        return playlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: playlistViewHolder, position: Int) {
        val playlist = playlists[position]

        holder.title.text = playlist.name
        holder.count.text = "${playlist.songs.size} ${if (playlist.songs.size == 1) "song" else "songs"}"

        holder.container.setOnClickListener {
            try{
                val intent = Intent(holder.itemView.context, playlistContent::class.java)
                intent.putExtra("name",playlist.name)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                holder.itemView.context.startActivity(intent)
                // Disable transition animations
            }catch(e:Exception){
                Log.e("folder click", "error in onclick in folder adapter: \n$e", )
            }
        }

        holder.container.setOnLongClickListener {
            val adapter = menu_playlist_adapter(playlist.name,"user",playlist.count,playlist.songs)
            adapter.show(fragmentManager, adapter.tag)
            true
        }


        holder.menu.setOnClickListener{
            val adapter = menu_playlist_adapter(playlist.name,"user",playlist.count,playlist.songs)
            adapter.show(fragmentManager, adapter.tag)
        }


    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    class playlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val count: TextView = itemView.findViewById(R.id.count)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val menu: ImageButton = itemView.findViewById(R.id.option)
        // Add other views if needed
    }
}
