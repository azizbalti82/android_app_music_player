// SongAdapter.kt
package com.example.playerbalti.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.menu.menu_playlist_add_adapter
import com.example.playerbalti.playlist
import com.example.playerbalti.storage.db_manager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class playlistSelectAdapter(
    private var playlists: List<playlist>,
    private var songs: List<Song>,
    private val fragmentManager: FragmentManager,
    val parent: menu_playlist_add_adapter,
) : RecyclerView.Adapter<playlistSelectAdapter.playlistSelectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): playlistSelectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist_select, parent, false)
        return playlistSelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: playlistSelectViewHolder, position: Int) {
        val playlist = playlists[position]

        holder.title.text = playlist.name
        holder.count.text = "${playlist.songs.size} ${if (playlist.songs.size == 1) "song" else "songs"}"

        //on clicking a playlist (adding to it)
        holder.container.setOnClickListener {
            try{
                //add songs to playlist
                CoroutineScope(Dispatchers.Main).launch {
                    for (i in songs) {
                        playlist.songs.add(i)
                    }
                    //remove duplications
                    playlist.songs = playlist.songs.distinct().toMutableList()

                    for (i in songs){
                        //add to playlist (in database)
                        db_manager.addSong(holder.itemView.context, i.id, holder.title.text.toString())
                    }
                }


                parent.dismiss()
                Toast.makeText(parent.context?.applicationContext, "added to playlist", Toast.LENGTH_SHORT).show()
            }catch(e:Exception){
                Log.e("error", "error in onclick in playlist select adapter: \n$e", )
            }
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    class playlistSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val count: TextView = itemView.findViewById(R.id.count)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        // Add other views if needed
    }
}
