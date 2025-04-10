// SongAdapter.kt
package com.example.playerbalti.adapters

import android.content.Intent
import android.os.Environment
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
import com.example.playerbalti.content.folderContent
import com.example.playerbalti.folder
import com.example.playerbalti.menu.menu_folder_adapter


class FolderAdapter(private var folders:List<folder>,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_directory, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.textViewTitle.text = folder.name
        holder.textViewArtist.text = "${folder.trackCount} songs . ${folder.path.substringAfterLast(Environment.getExternalStorageDirectory().path)}"

        holder.container.setOnClickListener {
            try{
                val intent = Intent(holder.itemView.context, folderContent::class.java)
                intent.putExtra("path",folder.path)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                holder.itemView.context.startActivity(intent)
                // Disable transition animations
            }catch(e:Exception){
                Log.e("folder click", "error in onclick in folder adapter: \n$e", )
            }
        }

        holder.container.setOnLongClickListener {
            val adapter = menu_folder_adapter(folder.name,folder.trackCount,folder.path)
            adapter.show(fragmentManager, adapter.tag)
            true
        }

        holder.menu.setOnClickListener{
            val adapter = menu_folder_adapter(folder.name,folder.trackCount,folder.path)
            adapter.show(fragmentManager, adapter.tag)
        }
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.folderName)
        val textViewArtist: TextView = itemView.findViewById(R.id.folderInfo)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val menu: ImageButton = itemView.findViewById(R.id.option)
        // Add other views if needed
    }
}
