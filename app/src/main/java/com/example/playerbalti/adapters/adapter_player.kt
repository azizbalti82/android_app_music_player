// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

        holder.art.setOnClickListener{
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
    }
}
