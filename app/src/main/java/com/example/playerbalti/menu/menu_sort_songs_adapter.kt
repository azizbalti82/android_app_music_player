package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuSortSongsBinding
import com.example.playerbalti.storage.shared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_sort_songs_adapter : BottomSheetDialogFragment() {
    lateinit var b: MenuSortSongsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuSortSongsBinding.inflate(inflater,container,false)

        //set values: ------------------------------------------------------------------------------
        //set ui
        setUi()


        //set listeners
        b.dir.setOnClickListener {
            //save new sort option:
            if(shared.songs_sort_type=="name-asc"){
                shared.set(requireContext(), "sort", "songs_sort_type","name-desc")
            }else if(shared.songs_sort_type=="name-desc"){
                shared.set(requireContext(), "sort", "songs_sort_type","name-asc")
            }
            else if(shared.songs_sort_type=="artist-asc"){
                shared.set(requireContext(), "sort", "songs_sort_type","artist-desc")
            }else if(shared.songs_sort_type=="artist-desc"){
                shared.set(requireContext(), "sort", "songs_sort_type","artist-asc")
            }
            else if(shared.songs_sort_type=="album-asc"){
                shared.set(requireContext(), "sort", "songs_sort_type","album-desc")
            }else if(shared.songs_sort_type=="album-desc"){
                shared.set(requireContext(), "sort", "songs_sort_type","album-asc")
            }else if(shared.songs_sort_type=="duration-asc"){
                shared.set(requireContext(), "sort", "songs_sort_type","duration-desc")
            }else if(shared.songs_sort_type=="duration-desc"){
                shared.set(requireContext(), "sort", "songs_sort_type","duration-asc")
            }else if(shared.songs_sort_type=="added-asc"){
                shared.set(requireContext(), "sort", "songs_sort_type","added-desc")
            }else if(shared.songs_sort_type=="added-desc"){
                shared.set(requireContext(), "sort", "songs_sort_type","added-asc")
            }else if(shared.songs_sort_type=="modified-asc"){
                shared.set(requireContext(), "sort", "songs_sort_type","modified-desc")
            }else if(shared.songs_sort_type=="modified-desc"){
                shared.set(requireContext(), "sort", "songs_sort_type","modified-asc")
            }

            data.dataChanged = true
            setUi()
        }

        b.containerTitle.setOnClickListener {
            sort("name")
        }
        b.containerAlbum.setOnClickListener {
            sort("album")
        }
        b.containerArtist.setOnClickListener {
            sort("artist")
        }
        b.containerDuration.setOnClickListener {
            sort("duration")
        }
        b.containerDate.setOnClickListener {
            sort("added")
        }
        b.containerDateModified.setOnClickListener{
            sort("modified")
        }
        b.containerRandom.setOnClickListener{
            shared.set(requireContext(), "sort", "songs_sort_type","random")
            //hide menu:
            this.dismiss()
        }
        return b.root
    }

    fun sort(c:String){
        //save new sort option:
        if(shared.songs_sort_type=="name-asc"
            || shared.songs_sort_type=="artist-asc"
            || shared.songs_sort_type=="album-asc"
            || shared.songs_sort_type=="duration-asc"
            || shared.songs_sort_type=="added-asc"
            || shared.songs_sort_type=="modified-asc") {
            shared.set(requireContext(), "sort", "songs_sort_type","$c-asc")
        }else{
            shared.set(requireContext(), "sort", "songs_sort_type","$c-desc")
        }
        data.dataChanged = true
        //hide menu:
        this.dismiss()
    }


    fun setUi(){
        if(shared.songs_sort_type=="name-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerTitle.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.songs_sort_type=="name-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerTitle.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.songs_sort_type=="artist-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerArtist.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.songs_sort_type=="artist-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerArtist.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }


        else if(shared.songs_sort_type=="album-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerAlbum.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.songs_sort_type=="album-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerAlbum.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.songs_sort_type=="duration-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerDuration.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        } else if(shared.songs_sort_type=="duration-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerDuration.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.songs_sort_type=="added-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerDate.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }else if(shared.songs_sort_type=="added-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerDate.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.songs_sort_type=="modified-asc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.up))
            b.containerDateModified.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        } else if(shared.songs_sort_type=="modified-desc"){
            b.dir.setImageDrawable(getDrawable(requireContext(), R.drawable.down))
            b.containerDateModified.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }

        else if(shared.songs_sort_type=="random"){
            b.dir.visibility = View.INVISIBLE
            b.containerRandom.setBackgroundColor(getColor(requireContext(),R.color.bottom_sheet_bg_selected))
        }
    }
}