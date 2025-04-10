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
import com.example.playerbalti.album
import com.example.playerbalti.content.albumContent
import com.example.playerbalti.data.Companion.extractAlbumArt
import com.example.playerbalti.menu.menu_album_adapter
import com.example.playerbalti.menu.menu_artist_adapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlbumsAdapter(private var albums:List<album>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_album, parent, false)
        return AlbumsViewHolder(view)
    }


    private fun loadArt(c: Context, songs:MutableList<Song>, artView: ImageView) {
        // Use coroutine to load album art
        CoroutineScope(Dispatchers.Main).launch {
            //extract image
            val albumArt = withContext(Dispatchers.IO) {
                var url: ByteArray? = null
                for (i in songs) {
                    Log.d("test", "loadArt: ${i.title}")
                    url = extractAlbumArt(i.path)
                    if (url != null) {
                        //if the url is valid
                        break
                    }
                }
                url
            }

                val default = ContextCompat.getDrawable(c, R.drawable.album)
                if (default != null) {
                    DrawableCompat.setTint(default, ContextCompat.getColor(c, R.color.text3))
                }

                //load image
                Glide.with(c)
                    .load(albumArt)
                    .error(default)
                    .placeholder(default)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Cache the image efficiently
                    .into(artView)
        }
    }

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        val album = albums[position]
        holder.textViewTitle.text = album.name
        holder.textViewArtist.text = album.artist

        loadArt(holder.itemView.context, album.songs, holder.art)

        holder.container.setOnClickListener {
            try{
                val intent = Intent(holder.itemView.context, albumContent::class.java)
                intent.putExtra("name",album.name)
                intent.putExtra("artist",album.artist)
                intent.putExtra("year",album.year)

                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.context as Activity,holder.art,"album")
                holder.itemView.context.startActivity(intent,option.toBundle())
            }catch(e:Exception){
                Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e", )
            }
        }

        holder.container.setOnLongClickListener {
            val adapter = menu_album_adapter(album.name,album.trackCount,album.artist)
            adapter.show(fragmentManager, adapter.tag)
            true
        }

        holder.menu.setOnClickListener{
            val adapter = menu_album_adapter(album.name,album.trackCount,album.artist)
            adapter.show(fragmentManager, adapter.tag)
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.albumName)
        val textViewArtist: TextView = itemView.findViewById(R.id.albumArtist)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val art:ImageView = itemView.findViewById(R.id.art)
        val menu: ImageButton = itemView.findViewById(R.id.option)
        // Add other views if needed
    }
}
