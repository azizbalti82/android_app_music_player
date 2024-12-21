// SongAdapter.kt
package com.example.playerbalti.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.artist
import com.example.playerbalti.content.artistContent
import com.example.playerbalti.data
import com.example.playerbalti.menu.menu_artist_adapter
import com.example.playerbalti.menu.menu_folder_adapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ArtistAdapter(private var artists:List<artist>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<ArtistAdapter.ArtistsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_artist, parent, false)
        return ArtistsViewHolder(view)
    }

    fun loadArt(c: Context, songs:MutableList<Song>, artView:ImageView) {
        // Use coroutine to load album art
        CoroutineScope(Dispatchers.Main).launch {
            val Art = withContext(Dispatchers.IO) {
                var url: ByteArray? = null
                for (i in songs) {
                    Log.d("test", "loadArt artist: ${i.title}")
                    url = data.extractAlbumArt(i.path)
                    if (url != null) {
                        break
                    }
                }
                url
            }

            val default = ContextCompat.getDrawable(
                c, R.drawable.artist
            )
            if (default != null) {
                DrawableCompat.setTint(default, ContextCompat.getColor(c, R.color.text3))
            }

            Glide.with(c)
                .load(Art)
                .error(default)
                .placeholder(default)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Cache the image efficiently
                .into(artView)
            //.transition(DrawableTransitionOptions.withCrossFade()) // Smooth crossfade transition

        }
    }

    override fun onBindViewHolder(holder: ArtistsViewHolder, position: Int) {
        val artist = artists[position]
        holder.name.text = artist.name
        holder.songCount.text ="${artist.countSongs} songs • ${artist.countAlbum} albums"

        loadArt(holder.itemView.context, artist.songs, holder.art)

        /* old technic of "load image": replaced with loadArt()
        var url:ByteArray? = null
        var songs = artist.songs
        for(i in songs){
            if(!i.path.isNullOrBlank()){
                url = data.extractAlbumArt(i.path)
                break
            }
        }
        data.loadImage(holder.itemView.context, url, holder.art,R.drawable.artist)
         */

        holder.container.setOnClickListener {
            try{
                val intent = Intent(holder.itemView.context, artistContent::class.java)
                intent.putExtra("artist",artist.name)
                intent.putExtra("songCount",artist.countSongs)
                intent.putExtra("albumCount",artist.countAlbum)

                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.context as Activity,holder.art,"artist")
                holder.itemView.context.startActivity(intent,option.toBundle())
            }catch(e:Exception){
                Log.e("mainActivitymsg", "error in onclick in artist adapter: \n$e", )
            }
        }

        holder.container.setOnLongClickListener {
            val adapter = menu_artist_adapter(artist.name,artist.countSongs)
            adapter.show(fragmentManager, adapter.tag)
            true
        }

        holder.menu.setOnClickListener{
            val adapter = menu_artist_adapter(artist.name,artist.countSongs)
            adapter.show(fragmentManager, adapter.tag)
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    class ArtistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.Artistname)
        val songCount: TextView = itemView.findViewById(R.id.songsCount)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val menu: ImageButton = itemView.findViewById(R.id.option)
        val art:ImageView = itemView.findViewById(R.id.art)
        // Add other views if needed
    }
}
