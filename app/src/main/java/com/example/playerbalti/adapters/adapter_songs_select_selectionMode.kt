// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.data
import com.example.playerbalti.menu.menu_playlist_add_adapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongSelectionModeAdapter(val parent:selectionMode,private var songs:List<Song>,private val count: TextView,private val select_all:ImageButton,private val menu:ImageButton) : RecyclerView.Adapter<SongSelectionModeAdapter.SongViewHolder>() {
    //an arrayList to hold selected songs
    val selectedSongs: MutableList<Song> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_songs_selection_mode, parent, false)
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

        //set the correct background depending on selecting statue
        if(selectedSongs.indexOf(song) == -1){
            holder.container.setBackgroundResource(R.color.background)
        }else{
            holder.container.setBackgroundResource(R.color.background2)
        }

        menu.setOnClickListener {
            // Create a PopupMenu
            val popupMenu = PopupMenu(menu.context,menu)

            // Inflate the menu resource (create your menu in res/menu folder)
            popupMenu.menuInflater.inflate(R.menu.selection_mode_options, popupMenu.menu)

            // Set a click listener for menu items
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item?.itemId) {
                    R.id.play-> {
                        if(selectedSongs.isNotEmpty()) {
                            data.playAll(selectedSongs)
                            parent.finish()
                        }
                        true
                    }
                    R.id.next -> {
                        if(selectedSongs.isNotEmpty()) {
                            data.addNextAll(parent, selectedSongs)
                            parent.finish()
                        }
                        true
                    }
                    R.id.add_to_playlist -> {
                        if(selectedSongs.isNotEmpty()) {
                            val adapter = menu_playlist_add_adapter(selectedSongs)
                            adapter.show(parent.supportFragmentManager, adapter.tag)
                        }
                        true
                    }
                    R.id.delete -> {
                        //delete:
                        true
                    }
                    // Add more cases for other menu items if needed
                    else -> false
                }
            }

            // Show the PopupMenu
            popupMenu.show()
        }
        select_all.setOnClickListener {
            if(selectedSongs.size == songs.size){
                selectedSongs.clear()
            }else{
                selectedSongs.clear()
                selectedSongs.addAll(songs)
            }
            notifyDataSetChanged()
            count.text = "${selectedSongs.size} selected"
        }

        holder.container.setOnClickListener{
            if(selectedSongs.indexOf(song) == -1){
                //song not selected: lets select it
                selectedSongs.add(song)
                holder.container.setBackgroundResource(R.color.background2)
                count.text = "${selectedSongs.size} selected"
            }else{
                //already selected: lets unselect it
                selectedSongs.remove(song)
                holder.container.setBackgroundResource(R.color.background)
                count.text = "${selectedSongs.size} selected"

                if(selectedSongs.isEmpty()){
                    parent.finish()
                }
            }
            notifyItemChanged(position)
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

        // Add other views if needed
    }
}
