// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.chooseTracks
import com.example.playerbalti.data
import com.example.playerbalti.storage.db_manager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongsPickAdapter(private var songs:List<Song>, val parent:chooseTracks,private val done:ImageButton, val playlistName:String) : RecyclerView.Adapter<SongsPickAdapter.SongViewHolder>() {
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

        done.setOnClickListener {
            if(selectedSongs.isEmpty()){
                Toast.makeText(holder.itemView.context, "select at least 1 song", Toast.LENGTH_SHORT).show()
            }else{
                //add songs to playlist
                CoroutineScope(Dispatchers.Main).launch {
                    //get old playlist:
                    var playlist = data.getPlaylistSongs(playlistName)
                    val old = playlist.count() //old size
                    //add selected songs
                    for(i in selectedSongs){
                        playlist.add(i)
                    }

                    //remove duplications
                    playlist = playlist.distinct().toMutableList()
                    val new = playlist.count() - old //added songs

                    if(new == 0){
                        //selected songs already exist in the playlist
                        Toast.makeText(holder.itemView.context, "selected songs already exists", Toast.LENGTH_SHORT).show()
                    }else{
                        for (i in playlist){
                            //add to playlist (in database)
                            db_manager.addSong(holder.itemView.context, i.id,playlistName)
                        }
                        Toast.makeText(holder.itemView.context, "$new song added", Toast.LENGTH_SHORT).show()
                    }

                }
                parent.finish()
            }
        }
        holder.container.setOnClickListener{
            if(selectedSongs.indexOf(song) == -1){
                //song not selected: lets select it
                selectedSongs.add(song)
                holder.container.setBackgroundResource(R.color.background2)
            }else{
                //already selected: lets unselect it
                selectedSongs.remove(song)
                holder.container.setBackgroundResource(R.color.background)
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
