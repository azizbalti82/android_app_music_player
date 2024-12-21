package com.example.playerbalti.menu

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.adapters.TabsSelectorAdapter
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuTabsSelectorBinding
import com.example.playerbalti.storage.shared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Collections


class menu_tabs_selector_adapter : BottomSheetDialogFragment() {
    lateinit var b: MenuTabsSelectorBinding
    var order_changed = false
    var tabs = mutableListOf<String>()


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if(order_changed){
            //save new order
            var result = tabs.joinToString(separator = "|")
            shared.set(requireContext(),"settingsGeneral","tabsOrder",result)
            //restart
            data.restart()
        } else if (data.tabsChanged){
            data.tabsChanged = false
            //restart
            data.restart()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuTabsSelectorBinding.inflate(inflater,container,false)

        //set values: ------------------------------------------------------------------------------
        //setUi()
        setTabs()

        b.reset.setOnClickListener {
            shared.set(requireContext(), "settingsGeneral", "hiddenTabs", "")
            shared.set(requireContext(),"settingsGeneral","tabsOrder","songs|albums|artists|playlists|folders|genres")
            data.restart()
        }

        return b.root
    }

    private fun setTabs() {
        // Find the RecyclerView in your layout
        var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerView)

        // Set up the LinearLayoutManager
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        //for optimization
        recyclerView.setHasFixedSize(true)

        // Set up the Adapter
        val adapter = TabsSelectorAdapter()
        recyclerView.adapter = adapter

        setDragItems(adapter,recyclerView)
    }

    fun setDragItems(adapter:TabsSelectorAdapter , recyclerView:RecyclerView){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //change the order in the list inside the adapter (tabs)
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(adapter.getTabs(), fromPosition, toPosition);
                /*
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(adapter.getTabs(), i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(adapter.getTabs(), i, i - 1)
                    }
                }

                 */

                // Notify the adapter of the move
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

                // Retrieve the updated list of items from the adapter
                tabs = adapter.getTabs()
                order_changed = true
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Handle swipe-to-dismiss if needed
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}