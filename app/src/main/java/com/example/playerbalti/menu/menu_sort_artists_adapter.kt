package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.playerbalti.R
import com.example.playerbalti.databinding.MenuSortArtistsBinding
import com.example.playerbalti.storage.shared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_sort_artists_adapter : BottomSheetDialogFragment() {
    lateinit var b: MenuSortArtistsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuSortArtistsBinding.inflate(inflater,container,false)

        //set values: ------------------------------------------------------------------------------
        //set ui
        setUi()


        //set listeners
        b.dir.setOnClickListener {
            //save new sort option:
            if(shared.artists_sort_type=="name-asc"){
                shared.set(requireContext(), "sort", "artists_sort_type","name-desc")
            }else if(shared.artists_sort_type=="name-desc"){
                shared.set(requireContext(), "sort", "artists_sort_type","name-asc")
            }else if(shared.artists_sort_type=="songs-count-asc"){
                shared.set(requireContext(), "sort", "artists_sort_type","songs-count-desc")
            }else if(shared.artists_sort_type=="songs-count-desc"){
                shared.set(requireContext(), "sort", "artists_sort_type","songs-count-asc")
            }else if(shared.artists_sort_type=="albums-count-asc"){
                shared.set(requireContext(), "sort", "artists_sort_type","albums-count-desc")
            }else if(shared.artists_sort_type=="albums-count-desc"){
                shared.set(requireContext(), "sort", "artists_sort_type","albums-count-asc")
            }

            setUi()
        }


        b.containerArtist.setOnClickListener{
            //save new sort option:
            if(shared.artists_sort_type=="name-asc" || shared.artists_sort_type=="songs-count-asc" || shared.artists_sort_type=="albums-count-asc") {
                shared.set(requireContext(), "sort", "artists_sort_type","name-asc")
            }else{
                shared.set(requireContext(), "sort", "artists_sort_type","name-desc")
            }
            //hide menu:
            this.dismiss()
        }

        b.containerAlbumsCount.setOnClickListener{
            //save new sort option:
            if(shared.artists_sort_type=="name-asc" || shared.artists_sort_type=="songs-count-asc" || shared.artists_sort_type=="albums-count-asc") {
                shared.set(requireContext(), "sort", "artists_sort_type","albums-count-asc")
            }else{
                shared.set(requireContext(), "sort", "artists_sort_type","albums-count-desc")
            }
            //hide menu:
            this.dismiss()
        }

        b.containerSongsCount.setOnClickListener{
            //save new sort option:
            if(shared.artists_sort_type=="name-asc" || shared.artists_sort_type=="songs-count-asc" || shared.artists_sort_type=="albums-count-asc") {
                shared.set(requireContext(), "sort", "artists_sort_type","songs-count-asc")
            }else{
                shared.set(requireContext(), "sort", "artists_sort_type","songs-count-desc")
            }
            //hide menu:
            this.dismiss()
        }
        return b.root
    }

    fun setUi(){
        if(shared.artists_sort_type=="name-asc"){
            b.containerArtist.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
        }
        else if(shared.artists_sort_type=="name-desc"){
            b.containerArtist.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
        }
        else if(shared.artists_sort_type=="songs-count-asc"){
            b.containerSongsCount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
        }
        else if(shared.artists_sort_type=="songs-count-desc"){
            b.containerSongsCount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
        }
        else if(shared.artists_sort_type=="albums-count-asc"){
            b.containerAlbumsCount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
        }
        else if(shared.artists_sort_type=="albums-count-desc"){
            b.containerAlbumsCount.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
        }
    }
}