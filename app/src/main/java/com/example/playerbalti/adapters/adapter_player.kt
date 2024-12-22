// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.browser.webviewActivity
import com.example.playerbalti.data
import com.example.playerbalti.otherActivities.editLyricsActivity


class PlayerAdapter(private var queue:MutableList<Song>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_player, parent, false)
        return PlayerViewHolder(view)
    }
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val song = queue[position]

        val url = data.extractAlbumArt(song.path)
        data.loadImage(holder.itemView.context, url, holder.art,R.drawable.default_song_image)

        //hide lyrics for every scroll
        holder.lyrics.visibility = View.INVISIBLE
        holder.art.visibility = View.VISIBLE
        if(data.getLyrics(song.path) == ""){
            holder.lyrics_text.text = "No lyrics found"
        }else{
            holder.lyrics_text.text = data.getLyrics(song.path)
        }

        var lastScrollY = 0
        holder.lyrics_scroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > lastScrollY) {
                // Scrolling down
                // Scrolling down: hide container
                holder.lyrics_buttons.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction { holder.lyrics_buttons.visibility = View.GONE }
                    .start()
            } else if (scrollY < lastScrollY) {
                // Scrolling up: show container
                holder.lyrics_buttons.visibility = View.VISIBLE
                holder.lyrics_buttons.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }

            // Update the last scroll position
            lastScrollY = scrollY
        }






        holder.art.setOnClickListener{
            if(data.getLyrics(song.path) == ""){
                holder.lyrics_text.text = "No lyrics found"
            }else{
                holder.lyrics_text.text = data.getLyrics(song.path)?.trim()
            }


            holder.art.visibility = View.INVISIBLE
            holder.lyrics.visibility = View.VISIBLE

        }

        //add on click listener to the lyrics container & lyrics scrollview to show or hide it
        holder.lyrics.setOnClickListener{
            holder.lyrics.visibility = View.INVISIBLE
            holder.art.visibility = View.VISIBLE
        }
        holder.lyrics_text.setOnClickListener{
            holder.lyrics.visibility = View.INVISIBLE
            holder.art.visibility = View.VISIBLE
        }


        //add listeners to lyrics buttons:
        holder.edit.setOnClickListener{
            try{
                val intent = Intent(holder.itemView.context, editLyricsActivity::class.java)
                intent.putExtra("path",song.path)
                intent.putExtra("title",song.title)
                intent.putExtra("artist",song.artist)

                holder.itemView.context.startActivity(intent)
            }catch(e:Exception){
                Log.e("mainActivitymsg", "error in onclick in artist adapter: \n$e", )
            }
        }
        holder.search.setOnClickListener{
            try{
                val intent = Intent(holder.itemView.context, webviewActivity::class.java)
                intent.putExtra("title",song.artist+" - "+song.title+" lyrics")
                intent.putExtra("url","https://www.google.com/search?q=")
                holder.itemView.context.startActivity(intent)
            }catch(e:Exception){
                Log.e("mainActivitymsg",e.toString())
            }
        }
    }
    override fun getItemCount(): Int {
        return queue.size
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val art: ImageView = itemView.findViewById(R.id.art)
        val lyrics: ConstraintLayout = itemView.findViewById(R.id.lyrics)
        val lyrics_text: TextView = itemView.findViewById(R.id.lyrics_text)

        val search:Button = itemView.findViewById(R.id.search)
        val edit:Button = itemView.findViewById(R.id.edit)

        val lyrics_scroll:ScrollView =  itemView.findViewById(R.id.lyrics_scrollview)
        val lyrics_buttons:LinearLayout = itemView.findViewById(R.id.lyrics_buttons)

    }
}
