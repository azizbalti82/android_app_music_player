// SongAdapter.kt
package com.example.playerbalti.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.data
import com.example.playerbalti.menu.menu_song_adapter
import com.example.playerbalti.storage.shared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongAdapter(private var songs:List<Song>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_songs, parent, false)
        return SongViewHolder(view)
    }



    fun loadArt(c:Context,path:String,artView:ImageView) {
        // Use coroutine to load album art
         CoroutineScope(Dispatchers.Main).launch {
            val albumArt = withContext(Dispatchers.IO) {
                data.extractAlbumArt(path)
            }

            val default = ContextCompat.getDrawable(
               c, R.drawable.default_song_image
            )
            if (default != null) {
                DrawableCompat.setTint(default,ContextCompat.getColor(c, R.color.text3))
            }

            Glide.with(c)
                .load(albumArt)
                .error(default)
                .placeholder(default)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Cache the image efficiently
                .into(artView)
                //.transition(DrawableTransitionOptions.withCrossFade()) // Smooth crossfade transition

        }
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.textViewTitle.text = song.title
        holder.textViewArtist.text = song.artist

        loadArt(holder.itemView.context,song.path,holder.art)

        holder.menu.setOnClickListener{
            val adapter = menu_song_adapter(song)
            adapter.show(fragmentManager, adapter.tag)
        }

        holder.container.setOnClickListener{
            if (shared.shuffle) {
                var selected = song
                //remove current song from list
                songs.drop(position)
                //shuffle the list
                var songs = data.shuffle(songs.toMutableList())
                //add selected song to the start of list
                songs.add(0, selected)
                //play:
                data.playAll(songs.toMutableList())
            } else {
                data.playAll(songs.toMutableList(), position)
            }
        }

        holder.container.setOnLongClickListener{
            //open the selection activity: send to it "list","current position"
            val intent = Intent(holder.itemView.context, selectionMode::class.java)
            //update selection_mode_songList
            data.songsSelectionMode = songs.toMutableList()
            intent.putExtra("pos", position)
            //start activity
            holder.itemView.context.startActivity(intent)
            // Override the pending transition to remove animation
            (holder.itemView.context as Activity).overridePendingTransition(0, 0)

            //return true to the event to notify that you consumed that event
            true
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container: LinearLayout = itemView.findViewById(R.id.container)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewArtist: TextView = itemView.findViewById(R.id.textViewArtist)
        val art: ImageView = itemView.findViewById(R.id.art)
        val menu: ImageButton = itemView.findViewById(R.id.option)


        // Add other views if needed
    }
}
