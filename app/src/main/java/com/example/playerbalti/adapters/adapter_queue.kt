// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.data
import com.example.playerbalti.menu.menu_queue_adapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class QueueAdapter(private var songs:List<Song>,private val fragmentManager: FragmentManager,val parent:menu_queue_adapter) : RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {
    lateinit var viewGroup: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        viewGroup = parent
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_queue, parent, false)
        return QueueViewHolder(view)
    }


    fun loadArt(c: Context, path: String, artView: ImageView) {
        // Use coroutine to load album art
        CoroutineScope(Dispatchers.Main).launch {
            val albumArt = withContext(Dispatchers.IO) {
                data.extractAlbumArt(path)
            }

            val default = ContextCompat.getDrawable(
                c, R.drawable.default_song_image
            )
            if (default != null) {
                DrawableCompat.setTint(default, ContextCompat.getColor(c, R.color.text3))
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

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val song = songs[position]
        holder.textViewTitle.text = song.title
        holder.textViewArtist.text = song.artist

        //new method of art loading
        loadArt(holder.itemView.context, song.path, holder.art)

        /* old method of art loading
        var url = data.extractAlbumArt(song.path)
        data.loadImage(holder.itemView.context, url, holder.art,R.drawable.default_song_image)
         */

        holder.remove.setOnClickListener {
            fun remove(){
                data.queue.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, data.queue.size)

                //remove it with fade animation
                val alpha = AlphaAnimation(1f,0f) //(start_alpha, end_alpha) from 1 to 0
                alpha.duration = 1000

                holder.itemView.startAnimation(alpha) //start animation
                holder.itemView.visibility = View.GONE
            }

            if(position == data.queueSelectedSong){
                remove()
                data.miniPlayerStatue("stop_play")
            }else{
                remove()
            }

            //if the queue is empty clear the player(stop the playing)
            if(data.queue.isEmpty()){
                clear_queue()
            }
        }
        holder.container.setOnClickListener {
            if (data.queueSelectedSong != position) {
                data.queueSelectedSong = position
                data.miniPlayerStatue("stop_play")
            }
        }
    }

    fun clear_queue(){
        data.stop()
        //clear playing queue
        data.queue.clear()
        //hide mini_player
        data.miniplayer?.visibility = View.INVISIBLE
        //reset related variables
        data.queueSelectedSong = -1
        data.queueSelectedIsPlaying = false

        //closing parent page
        parent.dismiss()
        //closing parent page (player)
        if(data.player_activity != null){
            data.player_activity!!.finish()
        }

        data.player_activity = null
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container: LinearLayout = itemView.findViewById(R.id.container)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewArtist: TextView = itemView.findViewById(R.id.textViewArtist)
        val art: ImageView = itemView.findViewById(R.id.art)
        val remove: ImageButton = itemView.findViewById(R.id.remove)

        // Add other views if needed
    }
}
