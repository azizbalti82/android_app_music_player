package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuBackgroundBinding
import com.example.playerbalti.databinding.MenuSortSongsBinding
import com.example.playerbalti.storage.shared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_background_adapter : BottomSheetDialogFragment() {
    lateinit var b: MenuBackgroundBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuBackgroundBinding.inflate(inflater,container,false)

        //set values: ------------------------------------------------------------------------------
        //set ui




        return b.root
    }

}