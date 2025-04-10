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
import com.example.playerbalti.content.genreContent
import com.example.playerbalti.genre
import com.example.playerbalti.menu.menu_folder_adapter
import com.example.playerbalti.menu.menu_genre_adapter


class GenresAdapter(private var genres:List<genre>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<GenresAdapter.GenresViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenresViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_genre, parent, false)
        return GenresViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenresViewHolder, position: Int) {
        val genre = genres[position]
        holder.name.text = genre.name
        holder.songCount.text = genre.count.toString() + " songs"


        holder.container.setOnClickListener {
            try{
                val intent = Intent(holder.itemView.context, genreContent::class.java)
                intent.putExtra("name",genre.name)

                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                holder.itemView.context.startActivity(intent)
            }catch(e:Exception){
                Log.e("mainActivitymsg", "error in onclick in genre adapter: \n$e", )
            }
        }

        holder.container.setOnLongClickListener {
            val adapter = menu_genre_adapter(genre.name,genre.count)
            adapter.show(fragmentManager, adapter.tag)
            true
        }

        holder.menu.setOnClickListener{
            val adapter = menu_genre_adapter(genre.name,genre.count)
            adapter.show(fragmentManager, adapter.tag)
        }






    }

    override fun getItemCount(): Int {
        return genres.size
    }

    class GenresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.genreName)
        val songCount: TextView = itemView.findViewById(R.id.songsCount)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val menu:ImageButton = itemView.findViewById(R.id.option)
        // Add other views if needed
    }
}
