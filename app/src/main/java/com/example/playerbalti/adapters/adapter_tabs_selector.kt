// SongAdapter.kt
package com.example.playerbalti.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.storage.shared


class TabsSelectorAdapter : RecyclerView.Adapter<TabsSelectorAdapter.ViewHolder>() {
    private var tabs = data.get_tabs()
    private var hidden_tabs = data.get_hidden_tabs()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_tabs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tab = tabs[position]
        holder.name.text = tab

        holder.check.isClickable = false

        //uncheck hidden tabs
        if(tab in hidden_tabs){
            holder.check.isChecked = false
        }


        //hide or show
        holder.container.setOnClickListener {
            //if this is the last tab checked don't do anything:
            val checked = holder.check.isChecked
            holder.check.isChecked = !(hidden_tabs.size < tabs.size && checked)
        }

        holder.check.setOnCheckedChangeListener { buttonView, isChecked ->
            //prepare the string that we gonna save
            if (isChecked) {
                if (hidden_tabs.contains(tab)) {
                    hidden_tabs.remove(tab)
                }
            } else {
                hidden_tabs.add(tab)
            }
            var result = hidden_tabs.joinToString(separator = "|")
            //save result to shared
            shared.set(holder.itemView.context, "settingsGeneral", "hiddenTabs", result)
            data.tabsChanged = true
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }

    fun getTabs():MutableList<String>{
        return tabs
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val check: CheckBox = itemView.findViewById(R.id.check)
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val drag:ImageButton = itemView.findViewById(R.id.drag)
        // Add other views if needed
    }
}
