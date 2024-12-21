package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuSortAlbumsBinding
import com.example.playerbalti.storage.shared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_sort_albums_adapter : BottomSheetDialogFragment() {
    lateinit var b: MenuSortAlbumsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuSortAlbumsBinding.inflate(inflater,container,false)

        //set values: ------------------------------------------------------------------------------
        //set ui
        setUi()


        //set listeners
        b.dir.setOnClickListener {
            //save new sort option:
            if(shared.albums_sort_type=="name-asc"){
                shared.set(requireContext(), "sort", "albums_sort_type","name-desc")
            }else if(shared.albums_sort_type=="name-desc"){
                shared.set(requireContext(), "sort", "albums_sort_type","name-asc")
            }else if(shared.albums_sort_type=="artist-asc"){
                shared.set(requireContext(), "sort", "albums_sort_type","artist-desc")
            }else if(shared.albums_sort_type=="artist-desc"){
                shared.set(requireContext(), "sort", "albums_sort_type","artist-asc")
            }else if(shared.albums_sort_type=="count-asc"){
                shared.set(requireContext(), "sort", "albums_sort_type","count-desc")
            }else if(shared.albums_sort_type=="count-desc"){
                shared.set(requireContext(), "sort", "albums_sort_type","count-asc")
            }else if(shared.albums_sort_type=="year-asc"){
                shared.set(requireContext(), "sort", "albums_sort_type","year-desc")
            }else if(shared.albums_sort_type=="year-desc"){
                shared.set(requireContext(), "sort", "albums_sort_type","year-asc")
            }

            data.dataChanged = true
            setUi()
        }


        b.containerTitle.setOnClickListener{
            //save new sort option:
            if(shared.albums_sort_type=="name-asc" || shared.albums_sort_type=="count-asc" || shared.albums_sort_type=="year-asc" || shared.albums_sort_type=="artist-asc") {
                shared.set(requireContext(), "sort", "albums_sort_type","name-asc")
            }else{
                shared.set(requireContext(), "sort", "albums_sort_type","name-desc")
            }
            data.dataChanged = true
            //hide menu:
            this.dismiss()
        }

        b.containerArtist.setOnClickListener{
            //save new sort option:
            if(shared.albums_sort_type=="name-asc" || shared.albums_sort_type=="count-asc" || shared.albums_sort_type=="year-asc" || shared.albums_sort_type=="artist-asc") {
                shared.set(requireContext(), "sort", "albums_sort_type","artist-asc")
            }else{
                shared.set(requireContext(), "sort", "albums_sort_type","artist-desc")
            }
            data.dataChanged = true
            //hide menu:
            this.dismiss()
        }

        b.containerYear.setOnClickListener{
            //save new sort option:
            if(shared.albums_sort_type=="name-asc" || shared.albums_sort_type=="count-asc" || shared.albums_sort_type=="year-asc" || shared.albums_sort_type=="artist-asc") {
                shared.set(requireContext(), "sort", "albums_sort_type","year-asc")
            }else{
                shared.set(requireContext(), "sort", "albums_sort_type","year-desc")
            }
            data.dataChanged = true
            //hide menu:
            this.dismiss()
        }

        b.containerSongscount.setOnClickListener{
            //save new sort option:
            if(shared.albums_sort_type=="name-asc" || shared.albums_sort_type=="count-asc" || shared.albums_sort_type=="year-asc" || shared.albums_sort_type=="artist-asc") {
                shared.set(requireContext(), "sort", "albums_sort_type","count-asc")
            }else{
                shared.set(requireContext(), "sort", "albums_sort_type","count-desc")
            }
            data.dataChanged = true
            //hide menu:
            this.dismiss()
        }
        return b.root
    }


    fun setUi(){
        if(shared.albums_sort_type=="name-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerTitle.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.albums_sort_type=="name-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerTitle.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.albums_sort_type=="artist-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerArtist.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.albums_sort_type=="artist-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerArtist.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }


        else if(shared.albums_sort_type=="count-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerSongscount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.albums_sort_type=="count-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerSongscount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.albums_sort_type=="year-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerYear.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        } else if(shared.albums_sort_type=="year-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerYear.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }
    }
}