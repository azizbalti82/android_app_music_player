package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuSortFoldersBinding
import com.example.playerbalti.storage.shared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_sort_folders_adapter : BottomSheetDialogFragment() {
    lateinit var b: MenuSortFoldersBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuSortFoldersBinding.inflate(inflater,container,false)

        //set values: ------------------------------------------------------------------------------
        //set ui
        setUi()


        //set listeners
        b.dir.setOnClickListener {
            //save new sort option:
            if(shared.folders_sort_type=="name-asc"){
                shared.set(requireContext(), "sort", "folders_sort_type","name-desc")
            }else if(shared.folders_sort_type=="name-desc"){
                shared.set(requireContext(), "sort", "folders_sort_type","name-asc")
            }else if(shared.folders_sort_type=="count-asc"){
                shared.set(requireContext(), "sort", "folders_sort_type","count-desc")
            }else if(shared.folders_sort_type=="count-desc"){
                shared.set(requireContext(), "sort", "folders_sort_type","count-asc")
            }
            data.dataChanged = true
            setUi()
        }


        b.containerTitle.setOnClickListener{
            //save new sort option:
            if(shared.folders_sort_type=="name-asc" || shared.folders_sort_type=="count-asc") {
                shared.set(requireContext(), "sort", "folders_sort_type","name-asc")
            }else{
                shared.set(requireContext(), "sort", "folders_sort_type","name-desc")
            }
            data.dataChanged = true
            //hide menu:
            this.dismiss()
        }

        b.containerSongsCount.setOnClickListener{
            //save new sort option:
            if(shared.folders_sort_type=="name-asc" || shared.folders_sort_type=="count-asc") {
                shared.set(requireContext(), "sort", "folders_sort_type","count-asc")
            }else{
                shared.set(requireContext(), "sort", "folders_sort_type","count-desc")
            }
            data.dataChanged = true
            //hide menu
            this.dismiss()
        }
        return b.root
    }

    fun setUi(){
        if(shared.folders_sort_type=="name-asc"){
            b.containerTitle.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            data.songsDir.sortBy { it.name }
        }else if(shared.folders_sort_type=="name-desc"){
            b.containerTitle.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            data.songsDir.sortByDescending { it.name }
        }else if(shared.folders_sort_type=="count-asc"){
            b.containerSongsCount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            data.songsDir.sortBy { it.trackCount }
        }else if(shared.folders_sort_type=="count-desc"){
            b.containerSongsCount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            data.songsDir.sortByDescending { it.trackCount }
        }
    }
}